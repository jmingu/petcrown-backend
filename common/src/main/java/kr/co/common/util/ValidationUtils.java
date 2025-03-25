package kr.co.common.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // 이메일 형식 검증
    public static String validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
        return email;
    }

    // 문자열 검증 (null, 공백, 길이 제한)
    public static String validateString(String value, int min, int max) {
        if (value == null) {
            throw new IllegalArgumentException("값이 없습니다.");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("빈값이 있습니다.");
        }

        if (min != 0 && max != 0) {
            if (value.length() < min || value.length() > max) {
                throw new IllegalArgumentException("값의 길이는 " + min + "자 이상, " + max + "자 이하여야 합니다.");
            }
        }
        return value;
    }
}
