package kr.co.api.user.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.ValidationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PhoneNumber {
    
    private final String value;
    
    public PhoneNumber(String phoneNumber) {
//        validatePhoneNumber(phoneNumber);
        this.value = phoneNumber;
    }
    
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        try {
            String[] parts = phoneNumber.split("-");
            if (parts.length != 3) {
                throw new PetCrownException(BusinessCode.INVALID_PHONE_NUMBER);
            }

            ValidationUtils.validateNameString(parts[0], "핸드폰 번호 첫번째 자리","Phone number first part",3, 3);
            ValidationUtils.validateNameString(parts[1], "핸드폰 번호 두번째 자리","Phone number second part",4, 4);
            ValidationUtils.validateNameString(parts[2], "핸드폰 번호 세번째 자리","Phone number third part",4, 4);
        }catch (Exception e) {
            throw new PetCrownException(BusinessCode.INVALID_PHONE_NUMBER);
        }

    }

    public static PhoneNumber of(String phoneNumber) {
        return new PhoneNumber(phoneNumber);
    }
    
    public String getValue() {
        return value;
    }

}