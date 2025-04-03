package kr.co.api.converter.pet;

import kr.co.api.adapter.in.dto.pet.PetRegistrationRequestDto;
import kr.co.api.adapter.in.dto.user.request.UserEmailRegistrationRequestDto;
import kr.co.api.adapter.out.persistence.repository.breed.jpa.JpaBreedRepository;
import kr.co.api.adapter.out.persistence.repository.standard.ownership.jpa.JpaOwnershipRepository;
import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.pet.PetEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PetConverter {
    private final JpaBreedRepository jpaBreedRepository;
    private final JpaOwnershipRepository jpaOwnershipRepository;
    private final JpaUserRepository jpaUserRepository;

    public Pet saveRequestDtoToDomain(PetRegistrationRequestDto dto, Long userId) {
        return Pet.savePetObject(dto.getBreedId(), dto.getCustomBreed(), dto.getOwnershipId() ,userId, dto.getName(), dto.getBirthDate(), dto.getGender(), dto.getProfileImageUrl(), dto.getMicrochipId(), dto.getDescription());
    }

    public PetEntity savePetToEntity(Pet pet) {
        log.debug("pet ==> {}", pet);
        return new PetEntity(
                pet.getUserId(),
                pet.getUserId(),
                pet.getPetId(),
                jpaBreedRepository.getReferenceById(pet.getBreedId()),
                pet.getCustomBreed(),
                jpaOwnershipRepository.getReferenceById(pet.getOwnershipId()),
                jpaUserRepository.getReferenceById(pet.getUserId()),
                pet.getName(),
                pet.getBirthDate(),
                pet.getGender(),
                pet.getWeight(),
                pet.getHeight(),
                pet.getIsNeutered(),
                pet.getProfileImageUrl(),
                pet.getMicrochipId(),
                pet.getDescription()
        );
    }
}
