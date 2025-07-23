package kr.co.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CodeEnum {
    SUCCESS(HttpStatus.OK.value(), "성공", "SUCCESS"),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "에러", "ERROR"),
    INVALID_TOKEN(440, "로그인 만료되었습니다", "Token is expired"),
    AUTHENTICATION_ERROR(441, "인증 에러", "Authentication error"), // 인증 에러, 재로그인으로 이동 안내
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 에러", "Internal Server Error"),;

    private final int code;
    private final String messageKo;
    private final String messageEn;
}
