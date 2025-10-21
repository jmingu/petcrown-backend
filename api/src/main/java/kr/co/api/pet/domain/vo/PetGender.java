package kr.co.api.pet.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PetGender {
    
    public enum PetGenderType {
        MALE("M"),
        FEMALE("F");
        
        private final String code;
        
        PetGenderType(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
        
        public static PetGenderType fromCode(String code) {
            for (PetGenderType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }
    }
    
    private final PetGenderType type;
    
    public PetGender(String genderCode) {
        this.type = PetGenderType.fromCode(genderCode);
    }
    
    public PetGender(PetGenderType type) {
        this.type = type;
    }
    
    public String getCode() {
        return type.getCode();
    }
    
    public PetGenderType getType() {
        return type;
    }
    
    public boolean isMale() {
        return type == PetGenderType.MALE;
    }
    
    public boolean isFemale() {
        return type == PetGenderType.FEMALE;
    }
    
    @Override
    public String toString() {
        return type.getCode();
    }
}