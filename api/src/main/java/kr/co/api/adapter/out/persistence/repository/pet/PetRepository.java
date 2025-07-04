package kr.co.api.adapter.out.persistence.repository.pet;

import kr.co.api.adapter.out.persistence.repository.pet.jpa.JpaPetRepository;
import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.converter.pet.PetDomainEntityConverter;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.entity.pet.PetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PetRepository implements PetRepositoryPort {

    private final JpaPetRepository jpaPetRepository;
    private final PetDomainEntityConverter petConverter;


    /**
     * 펫 등록
     */
    @Override
    public void savePet(Pet pet) {

        PetEntity petEntity = petConverter.toEntityPet(pet);
        log.debug("petEntity ==> {}", petEntity);
        jpaPetRepository.save(petEntity);
    }

    /**
     * 나의 펫 조회
     */
    @Override
    public List<MyPetResponseDto> findAllPetByUserId(Long userId) {
        List<MyPetResponseDto> myPetResponseDtos = jpaPetRepository.findByUser_UserIdAndDeleteYn(userId, "N");
        return myPetResponseDtos;
    }

    /**
     * 펫 단일 조회
     */
    @Override
    public Pet findPetById(Long petId) {
        Optional<PetEntity> entity = jpaPetRepository.findById(petId);
        // 펫이 존재하는지 확인
        if (entity.isPresent()) {
            PetEntity petEntity = entity.get();
            Pet pet = petConverter.toDomainPet(petEntity);

            return pet;
        }
        return null;
    }

    /**
     * 펫 정보 변경
     */
    @Override
    public void updateMyPet(Pet pet, Long petId, Long userId) {
        jpaPetRepository.updateMyPet(pet.getName(),
                pet.getGender(),
                pet.getBirthDate(),
                pet.getBreed().getBreedId(),
                userId,
                petId);
    }


    /**
     * 나의 펫 삭제
     */
    @Override
    public void deleteMyPet(Long petId, Long userId) {
        jpaPetRepository.deleteMyPet(petId, userId);
    }


}
