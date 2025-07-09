package kr.co.api.converter.pet;

import kr.co.api.adapter.in.dto.pet.request.MyPetChangeRequestDto;
import kr.co.api.adapter.in.dto.pet.request.PetRegistrationRequestDto;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static kr.co.common.enums.BusinessCode.MISSING_REQUIRED_VALUE;
import static kr.co.common.enums.BusinessCode.PET_NOT_FOUND;

@Component
@Slf4j
@RequiredArgsConstructor
public class PetDtoDomainConverter {

    /**
     * 펫 생성 Dto(PetRegistrationRequestDto)를 Pet 도메인 모델로 변환합니다.
     */
    public Pet registerPetDtoToDomain(PetRegistrationRequestDto dto, Long userId) {
        if (dto == null || userId == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }
        return Pet.SetPetObject(dto.getBreedId(), dto.getCustomBreed(), dto.getOwnershipId(), userId, dto.getName(), dto.getBirthDate(), dto.getGender(), dto.getProfileImageUrl(), dto.getMicrochipId(), dto.getDescription());
    }

    /**
     * 펫 정보 변경 Dto(MyPetChangeRequestDto)를 Pet 도메인 모델로 변환합니다.
     */
    public Pet changePetDtoToDomain(MyPetChangeRequestDto dto, Long userId) {
        if (dto == null || userId == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }
        return Pet.SetPetObject(dto.getBreedId(), dto.getCustomBreed(), dto.getOwnershipId(), userId, dto.getName(), dto.getBirthDate(), dto.getGender(), dto.getProfileImageUrl(), dto.getMicrochipId(), dto.getDescription());

    }
}

