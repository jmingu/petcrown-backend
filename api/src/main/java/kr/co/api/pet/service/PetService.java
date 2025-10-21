package kr.co.api.pet.service;

import kr.co.api.common.mapper.FileInfoMapper;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.api.pet.dto.command.SpeciesInfoDto;
import kr.co.api.pet.dto.command.BreedInfoDto;
import kr.co.api.pet.dto.command.PetModeInfoDto;
import kr.co.api.pet.mapper.PetMapper;
import kr.co.api.pet.mapper.PetModeMapper;
import kr.co.api.pet.domain.Pet;
import kr.co.api.pet.converter.domainEntity.PetDomainEntityConverter;
import kr.co.api.pet.domain.Breed;
import kr.co.api.pet.domain.Ownership;
import kr.co.api.pet.domain.vo.PetName;
import kr.co.api.pet.domain.vo.PetGender;
import kr.co.api.user.domain.model.User;
import kr.co.api.vote.mapper.VoteMapper;
import kr.co.common.entity.file.FileInfoEntity;
import kr.co.common.service.ObjectStorageService;
import kr.co.common.exception.PetCrownException;
import kr.co.common.entity.pet.PetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static kr.co.common.enums.BusinessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PetService {

    private static final String PET_REF_TABLE = "pet";

    private final PetMapper petMapper;
    private final PetModeMapper petModeMapper;
    private final VoteMapper voteMapper;
    private final FileInfoMapper fileInfoMapper;
    private final ObjectStorageService objectStorageService;
    private final PetDomainEntityConverter petDomainEntityConverter;

    /**
     * 펫 등록
     */
    @Transactional
    public void insertPet(PetRegistrationDto petRegistrationDto, MultipartFile image) {

        // 1. DTO → Domain 변환 (유효성 검증 포함)
        Pet pet = petDomainEntityConverter.toPet(petRegistrationDto);

        // 2. Domain → Entity 변환하여 저장 (이미지 URL 없이)
        PetEntity petEntity = petDomainEntityConverter.toPetEntity(pet);
        petMapper.insertPetEntity(petEntity);
        Long petId = petEntity.getPetId();

        // 3. 이미지 업로드 및 FileInfoEntity 저장
        if (image != null && !image.isEmpty()) {
            String imageUrl = objectStorageService.uploadFile(image, "profiles/user/" + petRegistrationDto.getUserId() + "/pet");

            FileInfoEntity fileInfoEntity = createFileInfoEntity(
                petId, "IMAGE", imageUrl, image, petRegistrationDto.getUserId()
            );
            fileInfoMapper.insertFileInfo(fileInfoEntity);

            log.info("Pet image registered: petId={}, imageUrl={}", petId, imageUrl);
        }

        log.info("Pet registered successfully: userId={}, petId={}", petRegistrationDto.getUserId(), petId);
    }
    /**
     * 나의 펫 목록 조회
     */
    public List<PetInfoDto> selectPetList(Long userId) {
        return petMapper.selectPetListByUserId(userId);
    }
    
    /**
     * 펫 단일 조회
     */
    public PetInfoDto selectPet(Long petId, Long userId) {
        PetInfoDto petInfoDto = petMapper.selectPetById(petId);
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
        PetInfoDto existingPetInfo = petMapper.selectPetById(petUpdateDto.getPetId());
        if (existingPetInfo == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 2. 접근 권한 확인
        if (!existingPetInfo.getUserId().equals(petUpdateDto.getUserId())) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 3. 기존 펫을 도메인 객체로 생성
        Pet existingPet = Pet.getPetAllFiled(
                existingPetInfo.getPetId(),
                Breed.getBreedAllFiled(existingPetInfo.getBreedId(), null, null),
                existingPetInfo.getCustomBreed(),
                new Ownership(existingPetInfo.getOwnershipId()),
                User.ofId(existingPetInfo.getUserId()),
                new PetName(existingPetInfo.getName()),
                existingPetInfo.getBirthDate(),
                new PetGender(existingPetInfo.getGender()),
                null, null,
                null,
                null,
                existingPetInfo.getMicrochipId(),
                existingPetInfo.getDescription()
        );

        // 4. DTO → Domain 변환하여 업데이트된 Pet 생성
        Pet updatedPet = petDomainEntityConverter.toPetForUpdate(petUpdateDto, existingPet);

        // 5. Domain → Entity 변환하여 저장
        PetEntity petEntity = petDomainEntityConverter.toPetEntityForUpdate(updatedPet);
        petMapper.updatePetEntity(petEntity);

        // 6. 이미지가 제공되면 새로 업로드 및 FileInfoEntity 처리
        if (image != null && !image.isEmpty()) {
            // 1) 기존 이미지 조회
            List<FileInfoEntity> existingFiles = fileInfoMapper.selectByRefTableAndRefId(PET_REF_TABLE, petUpdateDto.getPetId());

            // 2) 새 이미지 업로드
            String newImageUrl = objectStorageService.uploadFile(image, "profiles/user/" + petUpdateDto.getUserId() + "/pet");

            // 3) 새 이미지 FileInfoEntity 저장
            FileInfoEntity fileInfoEntity = createFileInfoEntity(
                petUpdateDto.getPetId(), "IMAGE", newImageUrl, image, petUpdateDto.getUserId()
            );
            fileInfoMapper.insertFileInfo(fileInfoEntity);
            log.info("새 이미지 업로드 및 DB 저장 완료: {}", newImageUrl);

            // 4) 새 이미지 저장 성공 후, 기존 이미지만 개별 삭제
            if (!existingFiles.isEmpty()) {
                for (FileInfoEntity existingFile : existingFiles) {
                    // DB에서 기존 파일 정보 논리 삭제 (개별)
                    fileInfoMapper.deleteById(existingFile.getFileId(), petUpdateDto.getUserId());

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

        PetInfoDto pet = petMapper.selectPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 삭제 권한 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 투표와 관련된 삭제 제약 확인 (주간 투표 기준)
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);

            kr.co.common.entity.vote.VoteWeeklyEntity existingWeeklyVote =
                voteMapper.selectVoteWeeklyByPetIdAndWeek(petId, weekStart);

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

        // FileInfoEntity 삭제 (이미지 파일도 함께 삭제)
        List<FileInfoEntity> existingFiles = fileInfoMapper.selectByRefTableAndRefId(PET_REF_TABLE, petId);
        if (!existingFiles.isEmpty()) {
            for (FileInfoEntity existingFile : existingFiles) {
                if (existingFile.getFileUrl() != null) {
                    try {
                        objectStorageService.deleteFileFromUrl(existingFile.getFileUrl());
                        log.info("Pet 이미지 파일 삭제 완료: {}", existingFile.getFileUrl());
                    } catch (Exception e) {
                        log.warn("Pet 이미지 파일 삭제 실패 (계속 진행): {}", e.getMessage());
                    }
                }
            }
            fileInfoMapper.deleteByRefTableAndRefId(PET_REF_TABLE, petId, userId);
        }

        petMapper.deletePet(petId, userId);

        log.info("Pet deleted successfully: petId={}, userId={}", petId, userId);
    }

    /**
     * FileInfoEntity 생성
     */
    private FileInfoEntity createFileInfoEntity(Long petId, String fileType, String fileUrl,
                                                MultipartFile file, Long createUserId) {
        LocalDateTime now = LocalDateTime.now();
        String fileName = extractFileNameFromUrl(fileUrl);
        String originalFileName = file.getOriginalFilename();

        return new FileInfoEntity(
                null,                       // fileId
                now,                        // createDate
                createUserId,               // createUserId
                now,                        // updatedDate
                createUserId,               // updateUserId
                null,                       // deleteDate
                null,                       // deleteUserId
                PET_REF_TABLE,              // refTable
                petId,                      // refId
                fileType,                   // fileType
                null,                       // sortOrder
                fileUrl,                    // fileUrl
                file.getSize(),             // fileSize
                file.getContentType(),      // mimeType
                fileName,                   // fileName
                originalFileName            // originalFileName
        );
    }

    /**
     * 종 목록 조회
     */
    public List<SpeciesInfoDto> getAllSpecies() {
        List<SpeciesInfoDto> speciesList = petMapper.selectAllSpecies();
        log.info("Species list retrieved: count={}", speciesList.size());
        return speciesList;
    }

    /**
     * 품종 목록 조회 (특정 종)
     */
    public List<BreedInfoDto> getBreedsBySpeciesId(Long speciesId) {
        List<BreedInfoDto> breedList = petMapper.selectBreedsBySpeciesId(speciesId);
        log.info("Breed list retrieved: speciesId={}, count={}", speciesId, breedList.size());
        return breedList;
    }

    /**
     * 펫 감정 모드 전체 목록 조회
     */
    public List<PetModeInfoDto> getAllPetModes() {
        List<PetModeInfoDto> petModeList = petModeMapper.selectAllPetModes();
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
}