package kr.co.api.domain.model.user.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Gender {
    
    public enum GenderType {
        MALE("M"),
        FEMALE("F");
        
        private final String code;
        
        GenderType(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
        
        public static GenderType fromCode(String code) {
            for (GenderType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }
    }
    
    private final GenderType type;
    
    public Gender(String genderCode) {
        this.type = GenderType.fromCode(genderCode);
    }
    
    public Gender(GenderType type) {
        this.type = type;
    }
    
    public String getCode() {
        return type.getCode();
    }
    
    public GenderType getType() {
        return type;
    }
    
    public boolean isMale() {
        return type == GenderType.MALE;
    }
    
    public boolean isFemale() {
        return type == GenderType.FEMALE;
    }
    
    @Override
    public String toString() {
        return type.getCode();
    }
}