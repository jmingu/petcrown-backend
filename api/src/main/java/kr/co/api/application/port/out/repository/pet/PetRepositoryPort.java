package kr.co.api.application.port.out.repository.pet;

import kr.co.api.domain.model.pet.Pet;

public interface PetRepositoryPort {

    /**
     * 펫 등록
     */
    void savePet(Pet pet);
}
