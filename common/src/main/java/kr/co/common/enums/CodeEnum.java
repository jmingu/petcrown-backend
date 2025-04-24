package kr.co.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CodeEnum {
    SUCCESS(HttpStatus.OK.value(), "성공", "SUCCESS"),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "에러", "ERROR"),
    INVALID_TOKEN(440, "토큰이 만료되었습니다", "Token is expired"),
    INVALID_TOKEN_ERROR(441, "유효하지 않은 토큰입니다", "Invalid token"),
    TOKEN_DECRYPTION_FAILED(442, "토큰 복호화 실패", "Token decryption failed"),
    AUTHENTICATION_REQUIRED(443, "인증이 필요합니다", "Authentication required"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 에러", "Internal Server Error"),;

    private int code;
    private final String messageKo;
    private final String messageEn;
}
