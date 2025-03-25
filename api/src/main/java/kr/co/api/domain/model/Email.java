package kr.co.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private Long emailVerificationId;
    private String email;              // 이메일
    private String verificationCode;   // 인증 코드
    private LocalDateTime expiresDate;  // 만료 시간

    public Email(String email, String verificationCode, LocalDateTime expiresDate) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiresDate = expiresDate;
    }
}


