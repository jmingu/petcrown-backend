package kr.co.api.user.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

@Getter
public class Gender {
    
    private final String value;
    
    private Gender(String gender) {
        validateGender(gender);
        this.value = gender;
    }

    public static Gender of(String gender) {
        return new Gender(gender);
    }
    
    private void validateGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        String upperGender = gender.trim().toUpperCase();
        if (!upperGender.equals("M") && !upperGender.equals("F")) {
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }
    }
    
    public boolean isMale() {
        return "M".equals(value.toUpperCase());
    }
    
    public boolean isFemale() {
        return "F".equals(value.toUpperCase());
    }
    
    @Override
    public String toString() {
        return value;
    }
}