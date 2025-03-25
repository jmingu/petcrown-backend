package kr.co.api.domain.model.user;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter
public class Password {
    private String password;
    private String passwordCheck;

    // 비밀번호만 받는 생성자
    public Password(String password) {
        this.password = password;
    }

    // 회원가입 시 받는 생성자
    public Password(String password, String passwordCheck) {

        this.password = ValidationUtils.validateString(password, 4, 15);
        this.passwordCheck = ValidationUtils.validateString(passwordCheck, 4, 15);
    }

    // 비밀번호가 일치하는지 확인하는 메서드
    public boolean isPasswordMatching() {
        return this.password.equals(this.passwordCheck);
    }
}

