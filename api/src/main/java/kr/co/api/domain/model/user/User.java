package kr.co.api.domain.model.user;

import lombok.Getter;

@Getter
public class User {
    private String email;
    private String name;
    private String password;
    private String passwordCheck;

    // 가입
    public User(String email, String name, String password, String passwordCheck) {
        this.email = validate(email, "email");
        this.name = validate(name, "name");
        this.password = validate(password, "password");
        this.passwordCheck = validate(passwordCheck, "passwordCheck");

        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("cannot");
        }
    }

    //가입 유효성 null, "", " " 검증
    private String validate(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty or blank");
        }
        return value;
    }
}
