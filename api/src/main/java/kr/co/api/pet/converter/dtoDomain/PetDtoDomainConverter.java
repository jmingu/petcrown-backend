package kr.co.api.pet.converter.dtoDomain;

import kr.co.api.pet.domain.Pet;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import org.springframework.stereotype.Component;

/**
 * Pet Command DTO ↔ 도메인 객체 변환
 * Service Layer와 Domain Layer 간의 변환을 담당
 */
@Component
public class PetDtoDomainConverter {

    /**
     * PetRegistrationDto를 Pet 도메인 객체로 변환
     *
     * @param dto 펫 등록용 CommandDto
     * @return Pet 도메인 객체
     */
    public Pet toPetForRegistration(PetRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        // 간단한 펫 등록 - 품종(또는 기타품종), 이름
        return Pet.SetPetObject(
                dto.getBreedId(),
                dto.getCustomBreed(),
                dto.getUserId(),
                dto.getName()
        );
    }

    /**
     * PetUpdateDto를 Pet 도메인 객체로 변환
     *
     * @param dto 펫 수정용 CommandDto
     * @return Pet 도메인 객체
     */
    public Pet toPetForUpdate(PetUpdateDto dto) {
        if (dto == null) {
            return null;
        }

        // 간단한 펫 수정 - 품종(또는 기타품종), 이름
        return Pet.SetPetObject(
                dto.getBreedId(),
                dto.getCustomBreed(),
                dto.getUserId(),
                dto.getName()
        );
    }
}