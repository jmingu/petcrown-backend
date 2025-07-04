package kr.co.api.application.port.out.repository.pet;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.domain.model.pet.Pet;

import java.util.List;

public interface PetRepositoryPort {

    /**
     * 펫 등록
     */
    void savePet(Pet pet);

    /**
     * 나의 펫 조회
     */
    List<MyPetResponseDto> findAllPetByUserId(Long userId);

    /**
     * 펫 단일 조회
     */
    Pet findPetById(Long petId);

    /**
     * 펫 정보 변경
     */
    void updateMyPet(Pet pet, Long petId, Long userId);

    /**
     * 나의 펫 삭제
     */
    void deleteMyPet(Long petId, Long userId);


}
