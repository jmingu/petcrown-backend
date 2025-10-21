package kr.co.api.pet.converter.domainEntity;

import kr.co.api.pet.domain.Pet;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import kr.co.api.pet.dto.response.MyPetResponseDto;
import kr.co.common.entity.pet.PetEntity;
import org.springframework.stereotype.Component;

/**
 * Pet 도메인과 Entity 간의 변환을 담당하는 Converter
 * Domain ↔ Entity 간의 변환을 수행
 */
@Component
public class PetDomainEntityConverter {

    /**
     * PetRegistrationDto → Pet Domain
     */
    public Pet toPet(PetRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        return Pet.SetPetObject(
                dto.getBreedId(),
                dto.getCustomBreed(),
                dto.getUserId(),
                dto.getName(),
                dto.getBirthDate(),
                dto.getGender(),
                dto.getMicrochipId(),
                dto.getDescription()
        );
    }

    /**
     * PetUpdateDto → Pet Domain (업데이트용)
     */
    public Pet toPetForUpdate(PetUpdateDto dto, Pet existingPet) {
        if (dto == null || existingPet == null) {
            return null;
        }

        return existingPet.updateBasicInfo(
                dto.getBreedId(),
                dto.getCustomBreed(),
                dto.getName(),
                dto.getBirthDate(),
                dto.getGender(),
                dto.getDescription(),
                dto.getMicrochipId()
        );
    }

    /**
     * Pet Domain → PetEntity
     */
    public PetEntity toPetEntity(Pet pet) {
        if (pet == null) {
            return null;
        }

        return PetEntity.createPetEntity(
                pet.getPetId(),
                pet.getBreed() != null ? pet.getBreed().getBreedId() : null,
                pet.getCustomBreed(),
                pet.getOwnership() != null ? pet.getOwnership().getOwnershipId() : null,
                pet.getUserId(),
                pet.getNameValue(),
                pet.getBirthDate(),
                pet.getGenderValue(),
                pet.getWeightValue(),
                pet.getHeightValue(),
                pet.getIsNeutered(),
                pet.getMicrochipId(),
                pet.getDescription()
        );
    }

    /**
     * Pet Domain → PetEntity (업데이트용)
     */
    public PetEntity toPetEntityForUpdate(Pet pet) {
        if (pet == null) {
            return null;
        }

        return PetEntity.createPetEntityForUpdate(
                pet.getPetId(),
                pet.getBreed() != null ? pet.getBreed().getBreedId() : null,
                pet.getCustomBreed(),
                pet.getOwnership() != null ? pet.getOwnership().getOwnershipId() : null,
                pet.getUserId(),
                pet.getNameValue(),
                pet.getBirthDate(),
                pet.getGenderValue(),
                pet.getWeightValue(),
                pet.getHeightValue(),
                pet.getIsNeutered(),
                pet.getMicrochipId(),
                pet.getDescription()
        );
    }

    /**
     * PetEntity → Pet Domain (기본 구현)
     */
    public Pet toPet(PetEntity entity) {
        if (entity == null) {
            return null;
        }

        // TODO: Pet 생성자가 준비되면 실제 변환 로직 구현
        return null;
    }

    /**
     * Pet Domain → MyPetResponseDto
     */
    public MyPetResponseDto toMyPetResponseDto(Pet pet) {
        if (pet == null) {
            return null;
        }

        // 기본 응답 DTO 반환 (필요시 구현)
        return new MyPetResponseDto();
    }
}