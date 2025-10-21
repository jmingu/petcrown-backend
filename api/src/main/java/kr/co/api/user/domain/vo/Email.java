package kr.co.api.user.domain.vo;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter
public class Email {

    private final String value;

    private Email(String email) {
        ValidationUtils.validateEmail(email);
        this.value = email;
    }

    public static Email of(String email) {
        return new Email(email);
    }


}