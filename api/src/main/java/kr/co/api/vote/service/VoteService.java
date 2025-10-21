package kr.co.api.vote.service;

import kr.co.api.common.mapper.FileInfoMapper;
import kr.co.api.user.domain.model.UserVoteCount;
import kr.co.api.user.mapper.EmailGuestMapper;
import kr.co.api.user.mapper.UserMapper;
import kr.co.api.user.mapper.UserVoteCountMapper;
import kr.co.api.vote.domain.VotingEmail;
import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.command.VoteListDto;
import kr.co.api.vote.dto.command.VoteRegistrationDto;
import kr.co.api.vote.dto.command.VoteUpdateDto;
import kr.co.api.vote.mapper.VoteHistoryMapper;
import kr.co.api.vote.mapper.VoteMapper;
import kr.co.common.entity.file.FileInfoEntity;
import kr.co.common.entity.user.EmailGuestEntity;
import kr.co.common.entity.user.UserEntity;
import kr.co.common.entity.user.UserVoteCountEntity;
import kr.co.common.entity.user.UserVoteCountHistoryEntity;
import kr.co.common.entity.vote.VoteFileInfoEntity;
import kr.co.common.entity.vote.VoteHistoryEntity;
import kr.co.common.entity.vote.VoteWeeklyEntity;
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

    private final VoteMapper voteMapper;
    private final VoteHistoryMapper voteHistoryMapper;
    private final UserMapper userMapper;
    private final EmailGuestMapper emailGuestMapper;
    private final UserVoteCountMapper userVoteCountMapper;
    private final ObjectStorageService objectStorageService;
    private final FileInfoMapper fileInfoMapper;

    /**
     * 투표 등록 (Weekly 투표만 등록)
     */
    @Transactional
    public void createVote(VoteRegistrationDto voteRegistrationDto, MultipartFile image) {

        // 1. 주별 투표 등록 제한 검증 (주 1회만 등록 가능 - DB date_trunc 사용)
        int currentWeekVoteCount = voteMapper.countWeeklyVoteRegistrationByUser(
            voteRegistrationDto.getUserId());

        if (currentWeekVoteCount > 0) {
            throw new PetCrownException(BusinessCode.WEEKLY_VOTE_REGISTRATION_LIMIT_EXCEEDED);
        }

        log.debug("Weekly vote registration validation passed: userId={}, currentWeekCount={}",
                voteRegistrationDto.getUserId(), currentWeekVoteCount);

        // 2. 이미지 처리 및 파일 정보 준비
        String imageUrl;
        String fileName;
        String originalFileName;
        Long fileSize;
        String mimeType;

        if (image != null && !image.isEmpty()) {
            // 새 이미지 업로드
            String folderPath = String.format("vote/user/%d", voteRegistrationDto.getUserId());
            imageUrl = objectStorageService.uploadFile(image, folderPath);
            fileName = extractFileNameFromUrl(imageUrl);
            originalFileName = image.getOriginalFilename();
            fileSize = image.getSize();
            mimeType = image.getContentType();
        } else if (voteRegistrationDto.getProfileImageUrl() != null) {
            // 기본 이미지 사용 - file_info에서 파일 정보 조회
            imageUrl = voteRegistrationDto.getProfileImageUrl();

            // URL로 file_info 조회 (pet 테이블의 이미지)
            List<FileInfoEntity> fileInfoList = fileInfoMapper.selectByRefTableAndRefId("pet", voteRegistrationDto.getPetId());
            FileInfoEntity sourceFileInfo = fileInfoList.stream()
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

        // 3. Weekly 투표 등록 (weekStartDate는 DB에서 자동 계산)
        VoteWeeklyEntity voteWeeklyEntity =
            new VoteWeeklyEntity(
                voteRegistrationDto.getUserId(),
                voteRegistrationDto.getUserId(),
                null,  // weekStartDate는 DB에서 date_trunc로 자동 설정
                voteRegistrationDto.getPetId(),
                0,
                0,
                voteRegistrationDto.getPetModeId()
            );
        voteMapper.insertVoteWeekly(voteWeeklyEntity);
        Long voteWeeklyId = voteWeeklyEntity.getVoteWeeklyId();

        // 4. 투표 파일 정보 등록
        VoteFileInfoEntity voteFileInfoEntity =
            new VoteFileInfoEntity(
                voteRegistrationDto.getUserId(),
                voteRegistrationDto.getUserId(),
                "vote_weekly",
                voteWeeklyId,
                "IMAGE",
                1,
                imageUrl,
                fileSize,
                mimeType,
                fileName,
                originalFileName
            );
        voteMapper.insertVoteFileInfo(voteFileInfoEntity);

        // 5. 사용자 투표 카운트 관리
        UserVoteCountEntity userVoteCountEntity = userVoteCountMapper.selectByUserId(voteRegistrationDto.getUserId());
        if (userVoteCountEntity == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        UserVoteCount userVoteCount = UserVoteCount.of(
            userVoteCountEntity.getUserId(),
            userVoteCountEntity.getVoteCount(),
            userVoteCountEntity.getCreateDate(),
            userVoteCountEntity.getCreateUserId(),
            userVoteCountEntity.getUpdateDate(),
            userVoteCountEntity.getUpdateUserId()
        );

        userVoteCount.validateForRegistrationIncrement();
        Integer beforeCount = userVoteCount.getVoteCount();
        Integer afterCount = userVoteCount.calculateIncrementedCount();

        userVoteCountMapper.incrementVoteCount(voteRegistrationDto.getUserId(), voteRegistrationDto.getUserId());
        UserVoteCountHistoryEntity historyEntity = new UserVoteCountHistoryEntity(
            voteRegistrationDto.getUserId(), 1, beforeCount, afterCount, voteRegistrationDto.getUserId()
        );
        userVoteCountMapper.insertVoteCountHistory(historyEntity);

        log.info("Vote created successfully: petId={}, userId={}, weeklyId={}",
            voteRegistrationDto.getPetId(), voteRegistrationDto.getUserId(), voteWeeklyId);
    }


    /**
     * 투표 목록 조회 (현재 주 Weekly 투표)
     */
    public VoteListDto getVotes(int page, int size) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);

        long offset = (long) page * size;
        List<VoteInfoDto> votes = voteMapper.selectVoteWeeklyList(weekStart, offset, size);
        int totalCount = voteMapper.selectVoteWeeklyListCount(weekStart);

        return new VoteListDto(votes, totalCount);
    }

    /**
     * 투표 상세 조회 (Weekly 투표)
     */
    public VoteInfoDto getVote(Long voteId) {
        VoteInfoDto voteDetail = voteMapper.selectVoteWeeklyDetail(voteId);
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
        VoteInfoDto existingVote = voteMapper.selectVoteWeeklyDetail(voteUpdateDto.getVoteId());
        if (existingVote == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 2. 권한 확인 (투표 등록한 사용자만 수정 가능)
        if (!existingVote.getUserId().equals(voteUpdateDto.getUserId())) {
            throw new PetCrownException(ACCESS_DENIED);
        }

        // 3. 이미지 처리 - 새 이미지 업로드 → DB UPDATE → 기존 이미지 삭제
        if (image != null && !image.isEmpty()) {
            // 1) 새 이미지 업로드
            String folderPath = String.format("vote/user/%d", voteUpdateDto.getUserId());
            String newImageUrl = objectStorageService.uploadFile(image, folderPath);

            // 2) 파일 정보 DB UPDATE (기존 URL 수정)
            String fileName = extractFileNameFromUrl(newImageUrl);
            String originalFileName = image.getOriginalFilename();

            VoteFileInfoEntity voteFileInfoEntity =
                new VoteFileInfoEntity(
                    voteUpdateDto.getUserId(),
                    voteUpdateDto.getUserId(),
                    "vote_weekly",
                    voteUpdateDto.getVoteId(),
                    "IMAGE",
                    1,
                    newImageUrl,
                    image.getSize(),
                    image.getContentType(),
                    fileName,
                    originalFileName
                );
            voteMapper.updateVoteFileInfo(voteFileInfoEntity);
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
        VoteWeeklyEntity updateEntity =
            new VoteWeeklyEntity(
                voteUpdateDto.getUserId(),
                voteUpdateDto.getUserId(),
                null, null, 0, 0, voteUpdateDto.getPetModeId()
            );
        voteMapper.updateVoteWeekly(updateEntity);

        log.info("Vote updated successfully: voteId={}, userId={}",
            voteUpdateDto.getVoteId(), voteUpdateDto.getUserId());
    }

    /**
     * 투표 삭제 (Weekly/Monthly 논리 삭제)
     */
    @Transactional
    public void deleteVote(Long voteId, Long userId) {

        // 1. 기존 Weekly 투표 조회 및 권한 확인
        VoteInfoDto existingVote = voteMapper.selectVoteWeeklyDetail(voteId);
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
        voteMapper.deleteVoteWeekly(voteId, userId);

        // 5. 파일 정보 논리 삭제
        voteMapper.deleteVoteFileInfo("vote_weekly", voteId, userId);

        // 6. Monthly 투표는 그대로 유지 (다른 주차 투표들이 영향을 받을 수 있음)

        log.info("Vote deleted successfully: voteId={}, userId={}", voteId, userId);
    }

    /**
     * 주간 투표하기 (Weekly 투표 카운트만 증가)
     */
    @Transactional
    public void castVoteWeekly(Long voteId, String email) {
        LocalDate today = LocalDate.now();

        // 1. Weekly 투표 존재 여부 확인
        VoteInfoDto voteInfo = voteMapper.selectVoteWeeklyDetail(voteId);
        if (voteInfo == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 2. 이메일 타입 결정 (회원/비회원)
        UserEntity user = userMapper.selectByEmail(email);
        VotingEmail votingEmail;
        if (user != null) {
            votingEmail = VotingEmail.createForMember(email, user.getUserId());
        } else {
            votingEmail = VotingEmail.createForGuest(email);
        }

        if (votingEmail.isMember()) {
            // 회원 투표 처리
            votingEmail.validateMemberVotingRight();

            // 중복 투표 검증
            int existingVoteCount = voteHistoryMapper.countTodayVoteByUser(
                    votingEmail.getUserId(), voteId, today, "WEEKLY");

            if (existingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 투표 기록 생성
            VoteHistoryEntity voteHistoryEntity = new VoteHistoryEntity(
                    votingEmail.getUserId(), voteId, today, "WEEKLY");
            voteHistoryMapper.insertVoteHistory(voteHistoryEntity);

            // Weekly 투표 카운트 증가
            voteMapper.updateVoteWeeklyCount(voteId, votingEmail.getUserId());

            // 사용자 투표 카운트 감소 (투표권 사용)
            decrementUserVoteCount(votingEmail.getUserId());

            log.info("Member weekly vote cast: userId={}, voteId={}", votingEmail.getUserId(), voteId);

        } else {
            // 비회원 투표 처리
            EmailGuestEntity emailGuest = emailGuestMapper.selectTodayVerifiedEmail(
                    votingEmail.getEmailAddress(), today);
            boolean isTodayVerified = (emailGuest != null);
            votingEmail.validateGuestVotingRight(isTodayVerified);

            // 중복 투표 검증
            int existingVoteCount = voteHistoryMapper.countTodayVoteByEmail(
                    votingEmail.getEmailAddress(), voteId, today, "WEEKLY");

            if (existingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 투표 기록 생성
            VoteHistoryEntity voteHistoryEntity = new VoteHistoryEntity(
                    votingEmail.getEmailAddress(), voteId, today, "WEEKLY");
            voteHistoryMapper.insertVoteHistory(voteHistoryEntity);

            // Weekly 투표 카운트 증가
            voteMapper.updateVoteWeeklyCount(voteId, null);

            log.info("Guest weekly vote cast: email={}, voteId={}", votingEmail.getEmailAddress(), voteId);
        }

        log.info("Weekly vote cast successfully: email={}, voteId={}, isMember={}",
                email, voteId, votingEmail.isMember());
    }

    /**
     * 월간 투표하기 (Monthly 투표 카운트만 증가)
     */
    @Transactional
    public void castVoteMonthly(Long voteId, String email) {
        LocalDate today = LocalDate.now();

        // 1. Weekly 투표로부터 petId 조회
        VoteInfoDto voteInfo = voteMapper.selectVoteWeeklyDetail(voteId);
        if (voteInfo == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 2. Monthly 투표 존재 여부 확인
        LocalDate monthStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
        kr.co.common.entity.vote.VoteMonthlyEntity monthlyVote =
            voteMapper.selectVoteMonthlyByPetIdAndMonth(voteInfo.getPetId(), monthStart);

        if (monthlyVote == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        // 3. 이메일 타입 결정 (회원/비회원)
        UserEntity user = userMapper.selectByEmail(email);
        VotingEmail votingEmail;
        if (user != null) {
            votingEmail = VotingEmail.createForMember(email, user.getUserId());
        } else {
            votingEmail = VotingEmail.createForGuest(email);
        }

        if (votingEmail.isMember()) {
            // 회원 투표 처리
            votingEmail.validateMemberVotingRight();

            // 중복 투표 검증
            int existingVoteCount = voteHistoryMapper.countTodayVoteByUser(
                    votingEmail.getUserId(), voteId, today, "MONTHLY");

            if (existingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 투표 기록 생성
            VoteHistoryEntity voteHistoryEntity = new VoteHistoryEntity(
                    votingEmail.getUserId(), voteId, today, "MONTHLY");
            voteHistoryMapper.insertVoteHistory(voteHistoryEntity);

            // Monthly 투표 카운트 증가
            voteMapper.updateVoteMonthlyCount(monthlyVote.getVoteMonthlyId(), votingEmail.getUserId());

            // 사용자 투표 카운트 감소 (투표권 사용)
            decrementUserVoteCount(votingEmail.getUserId());

            log.info("Member monthly vote cast: userId={}, voteId={}", votingEmail.getUserId(), voteId);

        } else {
            // 비회원 투표 처리
            EmailGuestEntity emailGuest = emailGuestMapper.selectTodayVerifiedEmail(
                    votingEmail.getEmailAddress(), today);
            boolean isTodayVerified = (emailGuest != null);
            votingEmail.validateGuestVotingRight(isTodayVerified);

            // 중복 투표 검증
            int existingVoteCount = voteHistoryMapper.countTodayVoteByEmail(
                    votingEmail.getEmailAddress(), voteId, today, "MONTHLY");

            if (existingVoteCount > 0) {
                throw new PetCrownException(BusinessCode.ALREADY_VOTED_TODAY);
            }

            // 투표 기록 생성
            VoteHistoryEntity voteHistoryEntity = new VoteHistoryEntity(
                    votingEmail.getEmailAddress(), voteId, today, "MONTHLY");
            voteHistoryMapper.insertVoteHistory(voteHistoryEntity);

            // Monthly 투표 카운트 증가
            voteMapper.updateVoteMonthlyCount(monthlyVote.getVoteMonthlyId(), null);

            log.info("Guest monthly vote cast: email={}, voteId={}", votingEmail.getEmailAddress(), voteId);
        }

        log.info("Monthly vote cast successfully: email={}, voteId={}, isMember={}",
                email, voteId, votingEmail.isMember());
    }

    /**
     * 사용자 투표 카운트 감소 (공통 로직)
     */
    private void decrementUserVoteCount(Long userId) {
        UserVoteCountEntity userVoteCountEntity = userVoteCountMapper.selectByUserId(userId);
        if (userVoteCountEntity == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        UserVoteCount userVoteCount = UserVoteCount.of(
            userVoteCountEntity.getUserId(),
            userVoteCountEntity.getVoteCount(),
            userVoteCountEntity.getCreateDate(),
            userVoteCountEntity.getCreateUserId(),
            userVoteCountEntity.getUpdateDate(),
            userVoteCountEntity.getUpdateUserId()
        );

        userVoteCount.validateForCastingDecrement();
        Integer beforeCount = userVoteCount.getVoteCount();
        Integer afterCount = userVoteCount.calculateDecrementedCount();

        userVoteCountMapper.decrementVoteCount(userId, userId);
        UserVoteCountHistoryEntity historyEntity = new UserVoteCountHistoryEntity(
            userId, -1, beforeCount, afterCount, userId
        );
        userVoteCountMapper.insertVoteCountHistory(historyEntity);
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