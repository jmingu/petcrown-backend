package kr.co.api.domain.model;

import kr.co.api.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private Long emailVerificationId;
    private User user;
    private String verificationCode;   // 인증 코드
    private LocalDateTime expiresDate;  // 만료 시간

    public Long getUserId() {
        return user.getUserId();
    }

    // 인증 코드 비교
    public boolean isVerificationCodeValid(String code) {
        // 전달된 인증 코드가 저장된 인증 코드와 일치하는지 비교
        return verificationCode != null && verificationCode.equals(code);
    }

    // 만료 시간 비교
    public boolean isExpired() {
        // 만료 시간이 현재시간보다 이전인가 비교
        return expiresDate != null && expiresDate.isBefore(LocalDateTime.now());
    }



}


