package kr.co.api.domain.service;

import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.user.User;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class PetDomainService {
    
    public void validatePetRegistration(Pet pet, User owner) {
        if (pet == null || owner == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        validatePetBusinessRules(pet);
        validateOwnershipRules(pet, owner);
    }
    
    public void validatePetUpdate(Pet existingPet, Pet updatedPet, User owner) {
        if (existingPet == null || updatedPet == null || owner == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (!canUserModifyPet(existingPet, owner)) {
            throw new PetCrownException(BusinessCode.PET_NOT_OWNED);
        }
        
        validatePetBusinessRules(updatedPet);
    }
    
    public boolean canUserModifyPet(Pet pet, User user) {
        if (pet == null || user == null) {
            return false;
        }
        
        return pet.getUserId().equals(user.getUserId());
    }
    
    public boolean canDeletePet(Pet pet, User user) {
        if (!canUserModifyPet(pet, user)) {
            return false;
        }
        
        return true;
    }
    
    public int calculatePetAge(Pet pet) {
        if (pet.getBirthDate() == null) {
            return 0;
        }
        
        return Period.between(pet.getBirthDate(), LocalDate.now()).getYears();
    }
    
    public boolean isPetPuppy(Pet pet) {
        return calculatePetAge(pet) < 1;
    }
    
    public boolean isPetSenior(Pet pet) {
        return calculatePetAge(pet) >= 7;
    }
    
    private void validatePetBusinessRules(Pet pet) {
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (pet.getBirthDate() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new PetCrownException(BusinessCode.INVALID_BIRTH_DATE);
        }
        
        if (pet.getGender() == null || pet.getGender().trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }
    }
    
    private void validateOwnershipRules(Pet pet, User owner) {
        if (pet.getUserId() != null && !pet.getUserId().equals(owner.getUserId())) {
            throw new PetCrownException(BusinessCode.INVALID_PET_OWNER);
        }
    }
}