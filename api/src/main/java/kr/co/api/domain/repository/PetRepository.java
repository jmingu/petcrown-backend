package kr.co.api.domain.repository;

import kr.co.api.domain.model.pet.Pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository {
    
    Pet save(Pet pet);
    
    Optional<Pet> findById(Long petId);
    
    List<Pet> findByUserId(Long userId);
    
    boolean existsById(Long petId);
    
    void delete(Pet pet);
    
    void deleteById(Long petId);
    
    long countByUserId(Long userId);
}