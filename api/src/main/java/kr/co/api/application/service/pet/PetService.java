package kr.co.api.application.service.pet;

import kr.co.api.application.port.in.pet.PetUseCase;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.domain.model.pet.Pet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService implements PetUseCase {

    private final PetRepositoryPort petRepositoryPort;

    /**
     * 펫 등록
     */
    @Override
    public void savePet(Pet pet) {
        petRepositoryPort.savePet(pet);
    }
}
