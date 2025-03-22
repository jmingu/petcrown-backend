package kr.co.api.adapter.in.dto.user;

import lombok.Getter;

@Getter
public class UserRegistrationRequestDto {
    private String email;
    private String name;
    private String password;
    private String passwordCheck;
}
