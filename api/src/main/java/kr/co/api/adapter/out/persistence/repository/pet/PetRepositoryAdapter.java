package kr.co.api.adapter.out.persistence.repository.pet;

import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.repository.PetRepository;
import kr.co.api.adapter.out.persistence.repository.pet.jpa.JpaPetRepository;
import kr.co.api.converter.pet.PetDomainEntityConverter;
import kr.co.common.entity.pet.PetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PetRepositoryAdapter implements PetRepository {
    
    private final JpaPetRepository jpaPetRepository;
    private final PetDomainEntityConverter petConverter;
    
    @Override
    public Pet save(Pet pet) {
        PetEntity petEntity = petConverter.toEntity(pet);
        PetEntity savedEntity = jpaPetRepository.save(petEntity);
        return petConverter.toDomain(savedEntity);
    }
    
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
    public boolean existsById(Long petId) {
        return jpaPetRepository.existsById(petId);
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
    public long countByUserId(Long userId) {
        return jpaPetRepository.countByUser_UserId(userId);
    }
}