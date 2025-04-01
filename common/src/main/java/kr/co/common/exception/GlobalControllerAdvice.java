package kr.co.common.exception;


import kr.co.common.entity.common.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(PetCrownException.class)
    public ResponseEntity<CommonResponseDto> applicationHandler(PetCrownException e) {
        e.printStackTrace();
        return ResponseEntity.status(e.getResultCode()).body(new CommonResponseDto(e.getResultCode(), e.getResultMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponseDto> exceptionHandler(RuntimeException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new CommonResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto> handlerException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new CommonResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR"));
    }
}
