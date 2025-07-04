package kr.co.api.application.service.pet;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.application.port.in.pet.PetUseCase;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.co.common.enums.BusinessCode.PET_NOT_FOUND;
import static kr.co.common.enums.BusinessCode.PET_NOT_OWNED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PetService implements PetUseCase {

    private final PetRepositoryPort petRepositoryPort;

    /**
     * 펫 등록
     */
    @Transactional
    @Override
    public void savePet(Pet pet) {
        petRepositoryPort.savePet(pet);
    }

    /**
     * 나의 펫 조회
     */
    @Override
    public List<MyPetResponseDto> getPet(Long userId) {
        return petRepositoryPort.findAllPetByUserId(userId);
    }

    /**
     * 펫 단일 조회
     */
    @Override
    public MyPetResponseDto getPetById(Long petId, Long userId) {
        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        return null;
    }


    /**
     * 펫 정보 변경
     */
    @Transactional
    @Override
    public void changeMyPet(Long petId, Pet changePet, Long userId) {

        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 내 소유 펫인지 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 변경된 정보로 펫 업데이트
        petRepositoryPort.updateMyPet(changePet, petId, userId);


    }

    /**
     * 나의 펫 삭제
     */
    @Transactional
    @Override
    public void removeMyPet(Long petId, Long userId) {
        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 내 소유 펫인지 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 펫 삭제
        petRepositoryPort.deleteMyPet(petId, userId);
    }



}
