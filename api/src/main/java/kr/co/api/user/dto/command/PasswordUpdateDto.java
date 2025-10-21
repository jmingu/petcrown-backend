package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 비밀번호 수정용 Command DTO
 */
@Getter
@AllArgsConstructor
public class PasswordUpdateDto {

    private final Long userId;
    private final String currentPassword;
    private final String newPassword;
    private final String newPasswordConfirm;
}