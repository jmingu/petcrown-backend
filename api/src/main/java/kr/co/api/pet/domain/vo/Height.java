package kr.co.api.pet.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
public class Height {
    
    private static final double MIN_HEIGHT = 1.0;
    private static final double MAX_HEIGHT = 200.0;
    
    private final BigDecimal value;
    
    public Height(Double height) {
        validateHeight(height);
        this.value = BigDecimal.valueOf(height).setScale(2, RoundingMode.HALF_UP);
    }
    
    public Height(BigDecimal height) {
        validateHeight(height.doubleValue());
        this.value = height.setScale(2, RoundingMode.HALF_UP);
    }
    
    private void validateHeight(Double height) {
        if (height == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
            throw new PetCrownException(BusinessCode.INVALID_HEIGHT_RANGE);
        }
    }
    
    public Double getDoubleValue() {
        return value.doubleValue();
    }
    
    @Override
    public String toString() {
        return value.toString() + "cm";
    }
}