package kr.co.api.domain.model.user.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.ValidationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PhoneNumber {
    
    private final String value;
    
    public PhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.value = phoneNumber;
    }
    
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        String[] parts = phoneNumber.split("-");
        if (parts.length != 3) {
            throw new PetCrownException(BusinessCode.INVALID_PHONE_NUMBER);
        }
        
        ValidationUtils.validateString(parts[0], 3, 3);
        ValidationUtils.validateString(parts[1], 4, 4);
        ValidationUtils.validateString(parts[2], 4, 4);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}