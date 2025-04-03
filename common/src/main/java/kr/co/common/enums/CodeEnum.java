package kr.co.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CodeEnum {
    SUCCESS(HttpStatus.OK.value(), "SUCCESS"),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR"),
    INVALID_TOKEN(440, "Token is invalid"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"),;

    private int code;
    private String message;
}
