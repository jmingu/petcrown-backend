package kr.co.common.entity.user;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class EmailVerificationEntity {

    private Long emailVerificationId;  // 이메일 인증 ID (자동 증가 PK)

    private Long userId; // 사용자 ID (FK 참조)

    private String verificationCode;  // 인증 코드

    private LocalDateTime expiresDate;  // 만료 시간 (ex: 10분 후)

    // BaseTimeEntity 필드들
    private LocalDateTime createDate; // 생성 일자
    private Long createUserId;  // 생성 ID
    private LocalDateTime updateDate; // 업데이트 일자
    private Long updateUserId;  // 수정ID

    
    /**
     * 인증 코드 만료 여부 확인
     */
    public boolean isExpired() {
        return this.expiresDate.isBefore(LocalDateTime.now());
    }

}
