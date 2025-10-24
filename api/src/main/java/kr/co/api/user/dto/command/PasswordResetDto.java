package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 비밀번호 찾기 Command DTO (서비스 레이어 내부용)
 */
@Getter
@AllArgsConstructor
public class PasswordResetDto {

    private final String email;
    private final String name;
}
