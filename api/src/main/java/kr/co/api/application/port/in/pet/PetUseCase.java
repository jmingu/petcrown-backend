package kr.co.api.application.port.in.pet;

import kr.co.api.domain.model.pet.Pet;

public interface PetUseCase {

    /**
     * 펫 등록
     */
    void savePet(Pet pet);
}
