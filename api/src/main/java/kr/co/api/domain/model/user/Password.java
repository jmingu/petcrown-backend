package kr.co.api.domain.model.user;

import kr.co.common.util.ValidationUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Password {
    private String password;
    private String passwordCheck;

    // 비밀번호만 받는 생성자
    public Password(String password) {
        this.password = password;
    }

    // 비밀번호 체크도 받는 생성자
    public Password(String password, String passwordCheck) {
        this.password = password;
        this.passwordCheck = passwordCheck;
    }


    // 인코딩 전 비밀번호 체크
    public static Password registerPassword(String password, String passwordCheck) {
        ValidationUtils.validateString(password, 4, 15);
        ValidationUtils.validateString(passwordCheck, 4, 15);
        return new Password(password, passwordCheck);
    }


    // 비밀번호가 일치하는지 확인하는 메서드
    public boolean isPasswordMatching() {
        return this.password.equals(this.passwordCheck);
    }
}

