package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 사용자 정보 수정용 Command DTO
 */
@Getter
@AllArgsConstructor
public class UserUpdateDto {

    private final Long userId;
    private final String name;
    private final String nickname;
    private final String gender;
    private final LocalDate birthDate;
    private final String phoneNumber;
}