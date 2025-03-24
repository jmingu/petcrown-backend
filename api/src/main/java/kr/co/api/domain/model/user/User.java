package kr.co.api.domain.model.user;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter
public class User {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String passwordCheck;

    // 가입
    public User(String email, String name, String nickname, String password, String passwordCheck) {
        this.email = ValidationUtils.validateString(email, 0, 0);
        this.name = ValidationUtils.validateString(name, 2, 10);
        this.nickname = ValidationUtils.validateString(nickname, 1, 10);
        this.password = ValidationUtils.validateString(password, 4, 15);
        this.passwordCheck = ValidationUtils.validateString(passwordCheck, 4, 15);

        // 이메일 패턴 확인
        this.email = ValidationUtils.validateEmail(this.email);

        // 패스워드 일치 확인
        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
    }

}
