package kr.co.api.domain.service;

import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.Email;
import kr.co.api.domain.model.user.vo.Nickname;
import kr.co.api.domain.model.user.vo.PhoneNumber;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {
    
    public void validateUserRegistration(User user) {
        if (user == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        validateUserBusinessRules(user);
    }
    
    public void validateUserUpdate(User existingUser, User updatedUser) {
        if (existingUser == null || updatedUser == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (!existingUser.getUserId().equals(updatedUser.getUserId())) {
            throw new PetCrownException(BusinessCode.INVALID_USER_UPDATE);
        }
        
        validateUserBusinessRules(updatedUser);
    }
    
    public boolean canUserAccessPet(User user, Long petOwnerId) {
        if (user == null || petOwnerId == null) {
            return false;
        }
        
        return user.getUserId().equals(petOwnerId);
    }
    
    public boolean isUserAdult(User user) {
        if (user.getBirthDate() == null) {
            return false;
        }
        
        return user.getBirthDate().plusYears(18).isBefore(java.time.LocalDate.now());
    }
    
    private void validateUserBusinessRules(User user) {
        if (user.getEmail() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (user.getName() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (user.getNickname() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
    }
}