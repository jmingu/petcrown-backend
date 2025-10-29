package kr.co.api.pet.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PetRepository {
    
//    public Pet savePet(Pet pet) {
//        PetEntity petEntity = petMapper.toPetEntity(pet);
//        PetEntity savedEntity = jpaPetRepository.save(petEntity);
//        return petMapper.toPet(savedEntity);
//    }
//
//    public Pet findPetById(Long petId) {
//        return jpaPetRepository.findById(petId)
//                .map(petMapper::toPet)
//                .orElse(null);
//    }
//
//    public List<MyPetResponseDto> findAllPetByUserId(Long userId) {
//        // JPA 레포지토리에서 직접 DTO로 반환하는 메소드 사용
//        // 만약 없다면 엔티티를 조회한 후 매퍼로 변환
//        List<PetEntity> petEntities = jpaPetRepository.findAllByUserId(userId);
//        return petEntities.stream()
//                .map(entity -> petMapper.toMyPetResponseDto(petMapper.toPet(entity)))
//                .toList();
//    }
//
//    public void updateMyPet(Pet pet, Long petId, Long userId) {
//        jpaPetRepository.updatePetInfo(
//                petId,
//                userId,
//                pet.getNameValue(),
//                pet.getBirthDate(),
//                pet.getGenderValue(),
//                pet.getDescription(),
//                pet.getMicrochipId()
//        );
//    }
//
//    public void deleteMyPet(Long petId, Long userId) {
//        jpaPetRepository.deleteByPetIdAndUserId(petId, userId);
//    }
}