package kr.co.api.pet.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.ValidationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PetName {
    
    private final String value;
    
    public PetName(String name) {
        validateName(name);
        this.value = name;
    }
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        ValidationUtils.validateString(name, 1, 20);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}