package kr.co.api.pet.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
public class Weight {
    
    private static final double MIN_WEIGHT = 0.1;
    private static final double MAX_WEIGHT = 200.0;
    
    private final BigDecimal value;
    
    public Weight(Double weight) {
        validateWeight(weight);
        this.value = BigDecimal.valueOf(weight).setScale(2, RoundingMode.HALF_UP);
    }
    
    public Weight(BigDecimal weight) {
        validateWeight(weight.doubleValue());
        this.value = weight.setScale(2, RoundingMode.HALF_UP);
    }
    
    private void validateWeight(Double weight) {
        if (weight == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new PetCrownException(BusinessCode.INVALID_WEIGHT_RANGE);
        }
    }
    
    public Double getDoubleValue() {
        return value.doubleValue();
    }

    public BigDecimal getValue() {
        return value;
    }

    public boolean isUnderweight() {
        return value.doubleValue() < 2.0;
    }
    
    public boolean isOverweight() {
        return value.doubleValue() > 50.0;
    }
    
    @Override
    public String toString() {
        return value.toString() + "kg";
    }
}