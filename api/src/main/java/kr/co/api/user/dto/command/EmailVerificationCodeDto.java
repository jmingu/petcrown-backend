package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmailVerificationCodeDto {

    private final Long userId;
    private final String email;
    private final String isEmailVerified;
    private final Long emailVerificationId;
    private final String verificationCode;
    private final LocalDateTime expiresDate;
}