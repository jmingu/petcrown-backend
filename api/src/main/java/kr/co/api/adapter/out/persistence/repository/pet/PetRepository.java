package kr.co.api.adapter.out.persistence.repository.pet;

import kr.co.api.adapter.out.persistence.repository.pet.jpa.JpaPetRepository;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.converter.pet.PetConverter;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.entity.pet.PetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PetRepository implements PetRepositoryPort {

    private final JpaPetRepository jpaPetRepository;
    private final PetConverter petConverter;

    @Transactional
    @Override
    public void savePet(Pet pet) {

        PetEntity petEntity = petConverter.savePetToEntity(pet);
        log.debug("petEntity ==> {}", petEntity);
        jpaPetRepository.save(petEntity);
    }
}
