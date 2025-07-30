package kr.co.api.adapter.out.persistence;

import kr.co.api.adapter.out.persistence.repository.pet.jpa.JpaPetRepository;
import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.converter.pet.PetDomainEntityConverter;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.port.out.LoadPetPort;
import kr.co.api.domain.port.out.SavePetPort;
import kr.co.common.entity.pet.PetEntity;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 펫 영속성 어댑터 (Output Adapter)
 * LoadPetPort, SavePetPort 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PetPersistenceAdapter implements LoadPetPort, SavePetPort {
    
    private final JpaPetRepository jpaPetRepository;
    private final PetDomainEntityConverter petConverter;
    
    // LoadPetPort 구현
    @Override
    public Optional<Pet> findById(Long petId) {
        return jpaPetRepository.findById(petId)
                .map(petConverter::toDomain);
    }
    
    @Override
    public List<Pet> findByUserId(Long userId) {
        return jpaPetRepository.findByUser_UserId(userId)
                .stream()
                .map(petConverter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MyPetResponseDto> findAllPetByUserId(Long userId) {
        return jpaPetRepository.findByUser_UserId(userId)
                .stream()
                .map(petEntity -> new MyPetResponseDto(
                        petEntity.getPetId(),
                        petEntity.getName(),
                        petEntity.getBirthDate().toString(),
                        petEntity.getGender(),
                        petEntity.getWeight(),
                        petEntity.getHeight(),
                        petEntity.getProfileImageUrl()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsById(Long petId) {
        return jpaPetRepository.existsById(petId);
    }
    
    @Override
    public long countByUserId(Long userId) {
        return jpaPetRepository.countByUser_UserId(userId);
    }
    
    // SavePetPort 구현
    @Override
    public Pet save(Pet pet) {
        PetEntity petEntity = petConverter.toEntity(pet);
        PetEntity savedEntity = jpaPetRepository.save(petEntity);
        return petConverter.toDomain(savedEntity);
    }
    
    @Override
    public void delete(Pet pet) {
        PetEntity petEntity = petConverter.toEntity(pet);
        jpaPetRepository.delete(petEntity);
    }
    
    @Override
    public void deleteById(Long petId) {
        jpaPetRepository.deleteById(petId);
    }
    
    @Override
    public void updatePet(Pet pet, Long petId, Long userId) {
        PetEntity existingEntity = jpaPetRepository.findById(petId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.PET_NOT_FOUND));
        
        // 소유자 확인
        if (!existingEntity.getUser().getUserId().equals(userId)) {
            throw new PetCrownException(BusinessCode.PET_NOT_OWNED);
        }
        
        // 업데이트할 필드들 설정
        existingEntity.updatePetInfo(
                pet.getNameValue(),
                pet.getGenderValue(),
                pet.getWeightValue(),
                pet.getHeightValue(),
                pet.getDescription()
        );
        
        jpaPetRepository.save(existingEntity);
    }
}