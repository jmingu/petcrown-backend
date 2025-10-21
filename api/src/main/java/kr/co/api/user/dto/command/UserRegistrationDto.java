package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserRegistrationDto {

    private final String email;

    private final String name;

    private final String nickname;

    private final String password;

    private final String passwordCheck;

    private final String phoneNumber;

    private final LocalDate birthDate;

    private final String gender;
}