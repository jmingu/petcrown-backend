package kr.co.common.util;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class ValidationUtils {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * 이메일 형식 검증
     */
    public static String validateEmail(String value) {

        if (value == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (value.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new PetCrownException(BusinessCode.INVALID_EMAIL_FORMAT);
        }
        return value;
    }


    /**
     * 문자열 검증 (null, 공백, 길이 제한)
     */
    public static void validateString(String value, int min, int max) {
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
    }

    /**
     * 문자열 검증 (null, 공백, 길이 제한)
     * koName, enName은 에러 메시지에 사용되는 이름
     */
    public static void validateNameString(String value, String koName, String enName, int min, int max) {
        if (value == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (value.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        if (min != 0 && max != 0) {
            log.debug("Validating string length: value='{}', min={}, max={}", value, min, max);
            if (value.length() < min || value.length() > max) {
                throw new PetCrownException(BusinessCode.INVALID_VALUE_LENGTH, koName, enName, min, max);
            }
        }
    }



}
