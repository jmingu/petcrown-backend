package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserInfoDto {

    private final Long userId;
    private final String email;
    private final String password;
    private final String name;
    private final String nickname;
    private final String phoneNumber;
    private final String profileImageUrl;
    private final LocalDate birthDate;
    private final String gender;
    private final String isEmailVerified;
}