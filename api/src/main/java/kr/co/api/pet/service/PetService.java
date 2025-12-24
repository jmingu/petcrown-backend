package kr.co.api.pet.service;

import kr.co.api.common.dto.FileInfoDto;
import kr.co.api.common.repository.CommonDateRepository;
import kr.co.api.common.repository.FileInfoRepository;
import kr.co.api.game.service.GameScoreService;
import kr.co.api.pet.domain.model.Breed;
import kr.co.api.pet.domain.model.Ownership;
import kr.co.api.pet.domain.model.Pet;
import kr.co.api.pet.domain.vo.PetGender;
import kr.co.api.pet.domain.vo.PetName;
import kr.co.api.pet.dto.command.*;
import kr.co.api.pet.repository.PetModeRepository;
import kr.co.api.pet.repository.PetRepository;
import kr.co.api.user.domain.model.User;
import kr.co.api.vote.dto.command.VoteWeeklyDto;
import kr.co.api.vote.repository.VoteRepository;
import kr.co.common.exception.PetCrownException;
import kr.co.common.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static kr.co.common.enums.BusinessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PetService {

    private static final String PET_REF_TABLE = "pet";

    private final PetRepository petRepository;
    private final PetModeRepository petModeRepository;
    private final VoteRepository voteRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ObjectStorageService objectStorageService;
    private final CommonDateRepository commonDateRepository;
    private final GameScoreService gameScoreService;

    /**
     * 펫 등록
     */
    @Transactional
    public void insertPet(PetRegistrationDto petRegistrationDto, MultipartFile image) {

        // 1. DTO → Domain 변환 (정적 팩토리 메서드 사용)
        Pet pet = Pet.SetPetObject(
                petRegistrationDto.getBreedId(),
                petRegistrationDto.getCustomBreed(),
                petRegistrationDto.getUserId(),
                petRegistrationDto.getName()
        );


        Long petId = petRepository.insertPetEntity(pet);

        // 3. 이미지 업로드 및 FileInfoEntity 저장
        if (image != null && !image.isEmpty()) {
            // pet/yyyymmdd_uuid(8자리) 경로 생성
            String folderPath = generatePetImagePath();
            String imageUrl = objectStorageService.uploadFile(image, folderPath);

            FileInfoDto fileInfoDto = createFileInfoDto(
                petId, "IMAGE", imageUrl, image, petRegistrationDto.getUserId()
            );
            Long fileId = fileInfoRepository.insertFileInfo(fileInfoDto);

            log.info("Pet image registered: petId={}, fileId={}, imageUrl={}", petId, fileId, imageUrl);
        }

        log.info("Pet registered successfully: userId={}, petId={}", petRegistrationDto.getUserId(), petId);
    }
    /**
     * 나의 펫 목록 조회
     */
    public List<PetInfoDto> selectPetList(Long userId) {
        return petRepository.selectPetListByUserId(userId);
    }
    
    /**
     * 펫 단일 조회
     */
    public PetInfoDto selectPet(Long petId, Long userId) {
        PetInfoDto petInfoDto = petRepository.selectPetById(petId);
        if (petInfoDto == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 접근 권한 확인
        if (!petInfoDto.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        return petInfoDto;
    }
    /**
     * 펫 정보 변경
     */
    @Transactional
    public void updatePet(PetUpdateDto petUpdateDto, MultipartFile image) {

        // 1. 기존 펫 존재 확인
        PetInfoDto existingPetInfo = petRepository.selectPetById(petUpdateDto.getPetId());
        if (existingPetInfo == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 2. 접근 권한 확인
        if (!existingPetInfo.getUserId().equals(petUpdateDto.getUserId())) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 3. 업데이트용 Pet 도메인 바로 생성
        Pet updatedPet = Pet.getPetAllFiled(
                petUpdateDto.getPetId(),
                petUpdateDto.getBreedId() != null ? Breed.getBreedAllFiled(petUpdateDto.getBreedId(), null, null) : null,
                petUpdateDto.getCustomBreed(),
                new Ownership(existingPetInfo.getOwnershipId()),  // ownership은 변경 불가
                User.ofId(petUpdateDto.getUserId()),
                petUpdateDto.getName() != null ? new PetName(petUpdateDto.getName()) : null,
                petUpdateDto.getBirthDate(),
                petUpdateDto.getGender() != null ? new PetGender(petUpdateDto.getGender()) : null,
                null, null,
                null,
                null,
                petUpdateDto.getMicrochipId(),
                petUpdateDto.getDescription()
        );

        petRepository.updatePetEntity(updatedPet);

        // 6. 이미지가 제공되면 새로 업로드 및 FileInfoDto 처리
        if (image != null && !image.isEmpty()) {
            // 1) 기존 이미지 조회
            List<FileInfoDto> existingFiles = fileInfoRepository.selectByRefTableAndRefId(PET_REF_TABLE, petUpdateDto.getPetId());

            // 2) 새 이미지 업로드 - pet/yyyymmdd_uuid(8자리) 경로 생성
            String folderPath = generatePetImagePath();
            String newImageUrl = objectStorageService.uploadFile(image, folderPath);

            // 3) 새 이미지 FileInfoDto 저장
            FileInfoDto fileInfoDto = createFileInfoDto(
                petUpdateDto.getPetId(), "IMAGE", newImageUrl, image, petUpdateDto.getUserId()
            );
            fileInfoRepository.insertFileInfo(fileInfoDto);
            log.info("새 이미지 업로드 및 DB 저장 완료: {}", newImageUrl);

            // 4) 새 이미지 저장 성공 후, 기존 이미지만 개별 삭제
            if (!existingFiles.isEmpty()) {
                for (FileInfoDto existingFile : existingFiles) {
                    // DB에서 기존 파일 정보 논리 삭제 (개별)
                    fileInfoRepository.deleteById(existingFile.getFileId(), petUpdateDto.getUserId());

                    // Object Storage에서 기존 파일 삭제
                    if (existingFile.getFileUrl() != null) {
                        try {
                            objectStorageService.deleteFileFromUrl(existingFile.getFileUrl());
                            log.info("기존 이미지 삭제 완료: {}", existingFile.getFileUrl());
                        } catch (Exception e) {
                            log.warn("기존 이미지 삭제 실패 (계속 진행): {}", e.getMessage());
                        }
                    }
                }
            }
        }

        log.info("Pet updated successfully: petId={}, userId={}", petUpdateDto.getPetId(), petUpdateDto.getUserId());
    }
    /**
     * 나의 펫 삭제
     */
    @Transactional
    public void deletePet(Long petId, Long userId) {

        PetInfoDto pet = petRepository.selectPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 삭제 권한 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 투표와 관련된 삭제 제약 확인 (주간 투표 기준)
        try {
            LocalDate weekStartDate = commonDateRepository.selectCurrentWeekStartDate();

            VoteWeeklyDto existingWeeklyVote =
                voteRepository.selectVoteWeeklyByPetIdAndWeek(petId, weekStartDate);

            // 현재 주에 활성화된 투표가 있으면 삭제 불가
            if (existingWeeklyVote != null) {
                throw new PetCrownException(VOTE_CANNOT_DELETE);
            }
        } catch (PetCrownException e) {
            throw e;
        } catch (Exception e) {
            // Vote 관련 체크에서 오류가 발생하면 로그만 남기고 계속 진행
            log.warn("Vote check failed during pet deletion, continuing with deletion: petId={}, error={}", petId, e.getMessage());
        }

        // FileInfoDto 삭제 (이미지 파일도 함께 삭제)
        List<FileInfoDto> existingFiles = fileInfoRepository.selectByRefTableAndRefId(PET_REF_TABLE, petId);
        if (!existingFiles.isEmpty()) {
            for (FileInfoDto existingFile : existingFiles) {
                if (existingFile.getFileUrl() != null) {
                    try {
                        objectStorageService.deleteFileFromUrl(existingFile.getFileUrl());
                        log.info("Pet 이미지 파일 삭제 완료: {}", existingFile.getFileUrl());
                    } catch (Exception e) {
                        log.warn("Pet 이미지 파일 삭제 실패 (계속 진행): {}", e.getMessage());
                    }
                }
            }
            fileInfoRepository.deleteByRefTableAndRefId(PET_REF_TABLE, petId, userId);
        }
        // 게임 점수 삭제
        gameScoreService.deleteScore(userId, petId);

        petRepository.deletePet(petId, userId);

        log.info("Pet deleted successfully: petId={}, userId={}", petId, userId);
    }

    /**
     * FileInfoDto 생성
     */
    private FileInfoDto createFileInfoDto(Long petId, String fileType, String fileUrl,
                                          MultipartFile file, Long createUserId) {
        String fileName = extractFileNameFromUrl(fileUrl);
        String originalFileName = file.getOriginalFilename();

        return new FileInfoDto(
                PET_REF_TABLE,              // refTable
                petId,                      // refId
                fileType,                   // fileType
                null,                       // sortOrder
                fileUrl,                    // fileUrl
                file.getSize(),             // fileSize
                file.getContentType(),      // mimeType
                fileName,                   // fileName
                originalFileName,           // originalFileName
                createUserId                // createUserId
        );
    }

    /**
     * 종 목록 조회
     */
    public List<SpeciesInfoDto> getAllSpecies() {
        List<SpeciesInfoDto> speciesList = petRepository.selectAllSpecies();
        log.info("Species list retrieved: count={}", speciesList.size());
        return speciesList;
    }

    /**
     * 품종 목록 조회 (특정 종)
     */
    public List<BreedInfoDto> getBreedsBySpeciesId(Long speciesId) {
        List<BreedInfoDto> breedList = petRepository.selectBreedsBySpeciesId(speciesId);
        log.info("Breed list retrieved: speciesId={}, count={}", speciesId, breedList.size());
        return breedList;
    }

    /**
     * 펫 감정 모드 전체 목록 조회
     */
    public List<PetModeInfoDto> getAllPetModes() {
        List<PetModeInfoDto> petModeList = petModeRepository.selectAllPetModes();
        log.info("PetMode list retrieved: count={}", petModeList.size());
        return petModeList;
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

    /**
     * 펫 이미지 폴더 경로 생성 (pet)
     */
    private String generatePetImagePath() {
        return "pet";
    }
}