package kr.co.common.util;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;

import java.util.regex.Pattern;


public class ValidationUtils {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // 이메일 형식 검증
    public static String validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new PetCrownException(BusinessCode.INVALID_EMAIL_FORMAT);
        }
        return email;
    }

    // 문자열 검증 (null, 공백, 길이 제한)
    public static String validateString(String value, int min, int max) {
        if (value == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (value.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        if (min != 0 && max != 0) {
            if (value.length() < min || value.length() > max) {
                throw new PetCrownException(BusinessCode.INVALID_LENGTH, min, max);
            }
        }
        return value;
    }



}
