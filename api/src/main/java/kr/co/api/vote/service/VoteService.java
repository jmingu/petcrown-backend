package kr.co.api.vote.service;

import kr.co.api.common.dto.FileInfoDto;
import kr.co.api.common.repository.FileInfoRepository;
import kr.co.api.pet.domain.model.Pet;
import kr.co.api.user.domain.model.User;
import kr.co.api.user.domain.model.UserVoteCount;
import kr.co.api.user.dto.command.EmailGuestDto;
import kr.co.api.user.dto.command.UserInfoDto;
import kr.co.api.user.dto.command.UserVoteCountDto;
import kr.co.api.user.domain.model.UserVoteCountHistory;
import kr.co.api.user.repository.EmailGuestRepository;
import kr.co.api.user.repository.UserRepository;
import kr.co.api.user.repository.UserVoteCountRepository;
import kr.co.api.vote.domain.model.VoteFileInfo;
import kr.co.api.vote.domain.model.VoteWeekly;
import kr.co.api.vote.domain.model.VotingEmail;
import kr.co.api.vote.dto.command.*;
import kr.co.api.vote.repository.VoteHistoryRepository;
import kr.co.api.vote.repository.VoteRepository;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static kr.co.common.enums.BusinessCode.ACCESS_DENIED;
import static kr.co.common.enums.BusinessCode.VOTE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteHistoryRepository voteHistoryRepository;
    private final UserRepository userRepository;
    private final EmailGuestRepository emailGuestRepository;
    private final UserVoteCountRepository userVoteCountRepository;
    private final ObjectStorageService objectStorageService;
    private final FileInfoRepository fileInfoRepository;

    /**
     * 투표 등록 (Weekly 투표만 등록)
     */
    @Transactional
    public void createVote(VoteRegistrationDto voteRegistrationDto, MultipartFile image) {

        // 1. 현재 주의 weekStartDate 조회 (DB date_trunc 사용)
        LocalDate weekStartDate = voteRepository.selectCurrentWeekStartDate();
        log.debug("Current week start date: {}", weekStartDate);

        // 2. petModeId 필수 검증
//        if (voteRegistrationDto.getPetModeId() == null) {
//            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
//        }

        // 3. 주별 투표 등록 제한 검증 (주 1회만 등록 가능)
        int currentWeekVoteCount = voteRepository.countWeeklyVoteRegistrationByUser(voteRegistrationDto.getUserId());

        if (currentWeekVoteCount > 0) {
            throw new PetCrownException(BusinessCode.WEEKLY_VOTE_REGISTRATION_LIMIT_EXCEEDED);
        }

        // 3. 이미지 처리 및 파일 정보 준비
        String imageUrl;
        String fileName;
        String originalFileName;
        Long fileSize;
        String mimeType;

        if (image != null && !image.isEmpty()) {
            // 새 이미지 업로드 - vote/{weekStartDate}/UUID 경로 사용
            String folderPath = String.format("vote/%s", weekStartDate.toString().replace("-", ""));
            imageUrl = objectStorageService.uploadFile(image, folderPath);
            fileName = extractFileNameFromUrl(imageUrl);
            originalFileName = image.getOriginalFilename();
            fileSize = image.getSize();
            mimeType = image.getContentType();
        } else if (voteRegistrationDto.getProfileImageUrl() != null) {
            // 기본 이미지 사용 - file_info에서 파일 정보 조회
            imageUrl = voteRegistrationDto.getProfileImageUrl();

            // URL로 file_info 조회 (pet 테이블의 이미지)
            List<FileInfoDto> fileInfoList = fileInfoRepository.selectByRefTableAndRefId("pet", voteRegistrationDto.getPetId());
            FileInfoDto sourceFileInfo = fileInfoList.stream()
                .filter(f -> imageUrl.equals(f.getFileUrl()))
                .findFirst()
                .orElse(null);

            if (sourceFileInfo != null) {
                fileName = sourceFileInfo.getFileName();
                originalFileName = sourceFileInfo.getOriginalFileName();
                fileSize = sourceFileInfo.getFileSize();
                mimeType = sourceFileInfo.getMimeType();
            } else {
                // file_info에서 찾지 못한 경우 기본값 사용
                fileName = extractFileNameFromUrl(imageUrl);
                originalFileName = null;
                fileSize = 0L;
                mimeType = "image/jpeg";
            }
        } else {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 3. 주별 투표 등록 제한 검증 (주 1회만 등록 가능) - 삭제 후 재 등록했을땐 투표수 증가 방지
        int currentWeekVoteCountNoDelete = voteRepository.countWeeklyVoteRegistrationByUserNoDelete(voteRegistrationDto.getUserId());

        // 4. CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        Pet pet = Pet.ofId(voteRegistrationDto.getPetId());
        User user = User.ofId(voteRegistrationDto.getUserId());
        VoteWeekly voteWeekly = VoteWeekly.createVote(
            pet,
            user,
            weekStartDate, voteRegistrationDto.getPetModeId() == null ? null : voteRegistrationDto.getPetModeId()
        );

        // 5. 영속성 저장 (도메인 객체 직접 전달)
        Long voteWeeklyId = voteRepository.insertVoteWeekly(voteWeekly);

        // 6. ID가 설정된 VoteWeekly 객체 생성
        VoteWeekly savedVoteWeekly = voteWeekly.withId(voteWeeklyId);

        // 7. 투표 파일 정보 도메인 생성 (정적 팩토리 메서드 사용)
        VoteFileInfo voteFileInfo = VoteFileInfo.createFileInfo(
            savedVoteWeekly,
            imageUrl,
            fileSize,
            mimeType,
            fileName,
            originalFileName
        );

        // 8.  저장 (도메인 객체 직접 전달)
        voteRepository.insertVoteFileInfo(voteFileInfo);

        // 6. 사용자 투표 카운트 관리
        UserVoteCountDto userVoteCountDto = userVoteCountRepository.selectByUserId(voteRegistrationDto.getUserId());
        if (userVoteCountDto == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        UserVoteCount userVoteCount = UserVoteCount.of(
                User.ofId(userVoteCountDto.getUserId()),
                userVoteCountDto.getVoteCount()
        );



        // 이번주 처음 등록일때만 투표수 증가 - 삭제 후 재 등록했을땐 투표수 증가 방지
        if (currentWeekVoteCountNoDelete == 0) {
            userVoteCountRepository.incrementVoteCount(voteRegistrationDto.getUserId());
            UserVoteCountHistory history = UserVoteCountHistory.create(
                    voteRegistrationDto.getUserId(), 1, userVoteCount.getVoteCount(), userVoteCount.getVoteCount() + 1
            );
            userVoteCountRepository.insertVoteCountHistory(history);
        }



        log.info("Vote created successfully: petId={}, userId={}, weeklyId={}",
            voteRegistrationDto.getPetId(), voteRegistrationDto.getUserId(), voteWeeklyId);
    }


    /**
     * 투표 목록 조회 (현재 주 Weekly 투표) - WHERE 절에 date_trunc 직접 사용
     */
    public VoteListDto getVotes(int page, int size) {
        long offset = (long) page * size;
        List<VoteInfoDto> votes = voteRepository.selectVoteWeeklyList(offset, size);
        int totalCount = voteRepository.selectVoteWeeklyListCount();

        return new VoteListDto(votes, totalCount);
    }

    /**
     * 투표 상세 조회 (Weekly 투표)
     */
    public VoteInfoDto getVote(Long voteId) {
        VoteInfoDto voteDetail = voteRepository.selectVoteWeeklyDetail(voteId);
        if (voteDetail == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        return voteDetail;
    }

    /**
     * 투표 수정 (Weekly 투표 이미지 변경)
     */
    @Transactional
    public void updateVote(VoteUpdateDto voteUpdateDto, MultipartFile image) {

        // 1. 기존 Weekly 투표 조회 및 권한 확인
        VoteInfoDto existingVote = voteRepository.selectVoteWeeklyDetail(voteUpdateDto.getVoteId());
        if (existingVote == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 2. 권한 확인 (투표 등록한 사용자만 수정 가능)
        if (!existingVote.getUserId().equals(voteUpdateDto.getUserId())) {
            throw new PetCrownException(ACCESS_DENIED);
        }

        // 3. petModeId 필수 검증
        if (voteUpdateDto.getPetModeId() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 4. 현재 주의 weekStartDate 조회 (이미지 경로용)
        LocalDate weekStartDate = voteRepository.selectCurrentWeekStartDate();

        // 4. 이미지 처리 - 새 이미지 업로드 → DB UPDATE → 기존 이미지 삭제
        if (image != null && !image.isEmpty()) {
            // 1) 새 이미지 업로드 - vote/{weekStartDate}/UUID 경로 사용
            String folderPath = String.format("vote/%s", weekStartDate.toString());
            String newImageUrl = objectStorageService.uploadFile(image, folderPath);

            // 2) 파일 정보 DB UPDATE (기존 URL 수정)
            String fileName = extractFileNameFromUrl(newImageUrl);
            String originalFileName = image.getOriginalFilename();

            VoteFileInfoDto voteFileInfoDto =
                new VoteFileInfoDto(
                    voteUpdateDto.getVoteId(),
                    newImageUrl,
                    image.getSize(),
                    image.getContentType(),
                    fileName,
                    originalFileName
                );
            voteRepository.updateVoteFileInfo(voteFileInfoDto);
            log.info("새 이미지 업로드 및 DB 수정 완료: {}", newImageUrl);

            // 3) DB 수정 성공 후, Object Storage에서 기존 이미지만 삭제
            if (existingVote.getProfileImageUrl() != null && !existingVote.getProfileImageUrl().isEmpty()) {
                try {
                    objectStorageService.deleteFileFromUrl(existingVote.getProfileImageUrl());
                    log.info("기존 이미지 삭제 완료: {}", existingVote.getProfileImageUrl());
                } catch (Exception e) {
                    log.warn("기존 이미지 삭제 실패 (계속 진행): {}", e.getMessage());
                }
            }
        }

        // 5. Weekly 투표 update_date 갱신
        VoteWeeklyDto updateDto = new VoteWeeklyDto(
                voteUpdateDto.getVoteId(),
                null,
                voteUpdateDto.getUserId(),
                null,
                null,
                null,
                voteUpdateDto.getPetModeId() != null ? voteUpdateDto.getPetModeId().intValue() : null
        );
        voteRepository.updateVoteWeekly(updateDto);

        log.info("Vote updated successfully: voteId={}, userId={}",
            voteUpdateDto.getVoteId(), voteUpdateDto.getUserId());
    }

    /**
     * 투표 삭제 (Weekly/Monthly 논리 삭제)
     */
    @Transactional
    public void deleteVote(Long voteId, Long userId) {

        // 1. 기존 Weekly 투표 조회 및 권한 확인
        VoteInfoDto existingVote = voteRepository.selectVoteWeeklyDetail(voteId);
        if (existingVote == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 2. 권한 확인 (투표 등록한 사용자만 삭제 가능)
        if (!existingVote.getUserId().equals(userId)) {
            throw new PetCrownException(ACCESS_DENIED);
        }

        // 3. 이미지 삭제
        if (existingVote.getProfileImageUrl() != null && !existingVote.getProfileImageUrl().isEmpty()) {
            try {
                objectStorageService.deleteFileFromUrl(existingVote.getProfileImageUrl());
                log.info("이미지 삭제 완료: {}", existingVote.getProfileImageUrl());
            } catch (Exception e) {
                log.warn("이미지 삭제 실패 (계속 진행): {}", e.getMessage());
            }
        }

        // 4. Weekly 투표 논리 삭제
        voteRepository.deleteVoteWeekly(voteId, userId);

        // 5. 파일 정보 논리 삭제
        voteRepository.deleteVoteFileInfo("vote_weekly", voteId, userId);

        // 6. Monthly 투표는 그대로 유지 (다른 주차 투표들이 영향을 받을 수 있음)

        log.info("Vote deleted successfully: voteId={}, userId={}", voteId, userId);
    }

    /**
     * 주간 투표하기 (Weekly 투표 카운트만 증가)
     * DB의 current_date 사용으로 서버/DB 시간대 불일치 방지
     */
    @Transactional
    public void castVoteWeekly(Long voteId, String email) {
        // 1. Weekly 투표 존재 여부 확인
        VoteInfoDto voteInfo = voteRepository.selectVoteWeeklyDetail(voteId);
        if (voteInfo == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 2. 자신의 펫에는 투표 불가
        if (email != null && email.equalsIgnoreCase(voteInfo.getOwnerEmail())) {
            throw new PetCrownException(BusinessCode.CANNOT_VOTE_OWN_PET);
        }

        // 3. 이메일 타입 결정 (회원/비회원)
        UserInfoDto user = userRepository.selectByEmail(email);
        VotingEmail votingEmail;
        if (user != null) {
            votingEmail = VotingEmail.createForMember(email, user.getUserId());
        } else {
            votingEmail = VotingEmail.createForGuest(email);
        }

        if (votingEmail.isMember()) {
            // 회원 투표 처리
            votingEmail.validateMemberVotingRight();

            // 중복 투표 검증 (DB의 current_date 사용)
            int existingVoteCount = voteHistoryRepository.countTodayVoteByUser(
                    votingEmail.getUser().getUserId(), voteId, "WEEKLY");

            if (existingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 가입전 이메일로 투표를 염두한 이메일 중복 검증
            int emailExistingVoteCount = voteHistoryRepository.countTodayVoteByEmail(
                    votingEmail.getEmail().getValue(), voteId, "WEEKLY");

            if (emailExistingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 투표 기록 생성 (historyDate는 Repository에서 current_date 사용)
            VoteHistoryDto voteHistoryDto = new VoteHistoryDto(
                    votingEmail.getUser().getUserId(), voteId, null, "WEEKLY");
            voteHistoryRepository.insertVoteHistory(voteHistoryDto);

            // Weekly 투표 카운트 증가
            voteRepository.updateVoteWeeklyCount(voteId, votingEmail.getUser().getUserId());

            // 사용자 투표 카운트 감소 (투표권 사용)
            decrementUserVoteCount(votingEmail.getUser().getUserId());

            log.info("Member weekly vote cast: userId={}, voteId={}", votingEmail.getUser().getUserId(), voteId);

        } else {
            // 비회원 투표 처리 (DB의 current_date 사용)
            EmailGuestDto emailGuest = emailGuestRepository.selectTodayVerifiedEmail(
                    votingEmail.getEmail().getValue());
            boolean isTodayVerified = (emailGuest != null);
            votingEmail.validateGuestVotingRight(isTodayVerified);

            // 중복 투표 검증 (DB의 current_date 사용)
            int existingVoteCount = voteHistoryRepository.countTodayVoteByEmail(
                    votingEmail.getEmail().getValue(), voteId, "WEEKLY");

            if (existingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 투표 기록 생성 (historyDate는 Repository에서 current_date 사용)
            VoteHistoryDto voteHistoryDto = new VoteHistoryDto(
                    null, voteId, votingEmail.getEmail().getValue(), "WEEKLY");
            voteHistoryRepository.insertVoteHistory(voteHistoryDto);

            // Weekly 투표 카운트 증가
            voteRepository.updateVoteWeeklyCount(voteId, null);

            log.info("Guest weekly vote cast: email={}, voteId={}", votingEmail.getEmail().getValue(), voteId);
        }

        log.info("Weekly vote cast successfully: email={}, voteId={}, isMember={}",
                email, voteId, votingEmail.isMember());
    }


    /**
     * 사용자 투표 카운트 감소 (공통 로직)
     */
    private void decrementUserVoteCount(Long userId) {
        UserVoteCountDto userVoteCountDto = userVoteCountRepository.selectByUserId(userId);
        if (userVoteCountDto == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        UserVoteCount userVoteCount = UserVoteCount.of(
            User.ofId(userVoteCountDto.getUserId()),
            userVoteCountDto.getVoteCount()
        );

        userVoteCount.validateForCastingDecrement();
        Integer beforeCount = userVoteCount.getVoteCount();
        Integer afterCount = userVoteCount.calculateDecrementedCount();

        userVoteCountRepository.decrementVoteCount(userId, userId);
        UserVoteCountHistory history = UserVoteCountHistory.create(
            userId, -1, beforeCount, afterCount
        );
        userVoteCountRepository.insertVoteCountHistory(history);
    }

    /**
     * URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        // URL에서 마지막 '/' 이후의 파일명 추출
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < fileUrl.length() - 1) {
            return fileUrl.substring(lastSlashIndex + 1);
        }
        return fileUrl;
    }

}