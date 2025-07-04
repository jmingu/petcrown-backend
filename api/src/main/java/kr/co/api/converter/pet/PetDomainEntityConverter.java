package kr.co.api.converter.pet;

import kr.co.api.adapter.out.persistence.repository.breed.jpa.JpaBreedRepository;
import kr.co.api.adapter.out.persistence.repository.standard.ownership.jpa.JpaOwnershipRepository;
import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.converter.user.UserDomainEntityConverter;
import kr.co.api.domain.model.pet.Breed;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.standard.ownership.Ownership;
import kr.co.api.domain.model.standard.species.Species;
import kr.co.common.entity.pet.BreedEntity;
import kr.co.common.entity.pet.PetEntity;
import kr.co.common.entity.standard.ownership.OwnershipEntity;
import kr.co.common.entity.standard.species.SpeciesEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PetDomainEntityConverter {

    private final UserDomainEntityConverter userDomainEntityConverter;
    private final JpaBreedRepository jpaBreedRepository;
    private final JpaOwnershipRepository jpaOwnershipRepository;
    private final JpaUserRepository jpaUserRepository;

    /**
     * Pet 도메인 모델을 PetEntity로 변환합니다.
     */
    public PetEntity toEntityPet(Pet pet) {
        if (pet == null) {
            return null;
        }

        return PetEntity.createPetEntity(
                pet.getPetId(),
                jpaBreedRepository.getReferenceById(pet.getBreed().getBreedId()),
                pet.getCustomBreed(),
                jpaOwnershipRepository.getReferenceById(pet.getOwnership().getOwnershipId() == null ? 1 : pet.getOwnership().getOwnershipId()),
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

    /**
     * BreedEntity를 Breed 도메인 모델로 변환합니다.
     */
    public Breed toDomainBreed(BreedEntity breedEntity) {
        if (breedEntity == null) {
            return null;
        }
        return Breed.getBreedAllFiled(
                breedEntity.getBreedId(),
                toDomainSpecies(breedEntity.getSpecies()),
                breedEntity.getName()
        );
    }

    /**
     * SpeciesEntity를 Species 도메인 모델로 변환합니다.
     */
    public Species toDomainSpecies(SpeciesEntity speciesEntity) {
        if (speciesEntity == null) {
            return null;
        }
        return new Species(
                speciesEntity.getSpeciesId(),
                speciesEntity.getName()
        );
    }

    /**
     * OwnershipEntity를 Ownership 도메인 모델로 변환합니다.
     */
    public Ownership toDomainOwnership(OwnershipEntity ownershipEntity) {
        if (ownershipEntity == null) {
            return null;
        }
        return new Ownership(
                ownershipEntity.getOwnershipId(),
                ownershipEntity.getOwnershipName()
        );
    }

    /**
     * PetEntity를 Pet 도메인 모델로 변환합니다.(전체)
     */
     public Pet toDomainPet(PetEntity petEntity) {
        if (petEntity == null) {
            return null;
        }
        return Pet.getPetAllFiled(
                petEntity.getPetId(),
                toDomainBreed(petEntity.getBreed()),
                petEntity.getCustomBreed(),
                toDomainOwnership(petEntity.getOwnership()),
                userDomainEntityConverter.toDomainBasic(petEntity.getUser()),
                petEntity.getName(),
                petEntity.getBirthDate(),
                petEntity.getGender(),
                petEntity.getWeight(),
                petEntity.getHeight(),
                petEntity.getIsNeutered(),
                petEntity.getProfileImageUrl(),
                petEntity.getMicrochipId(),
                petEntity.getDescription()
        );
     }

    /**
     * PetEntity Pet 도메인 모델로 변환합니다.(객체중 User 만)
     */
    public Pet toDomainPetWithUserOnly(PetEntity petEntity) {
        if (petEntity == null) {
            return null;
        }

        return Pet.getPetAllFiled(
                petEntity.getPetId(),
                null,
                petEntity.getCustomBreed(),
                null,
                userDomainEntityConverter.toDomainBasic(petEntity.getUser()),
                petEntity.getName(),
                petEntity.getBirthDate(),
                petEntity.getGender(),
                petEntity.getWeight(),
                petEntity.getHeight(),
                petEntity.getIsNeutered(),
                petEntity.getProfileImageUrl(),
                petEntity.getMicrochipId(),
                petEntity.getDescription()
        );

    }

}
