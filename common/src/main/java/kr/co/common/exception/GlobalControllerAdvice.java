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
    public ResponseEntity<?> applicationHandler(PetCrownException e) {
        e.printStackTrace();
        return CommonResponseDto.error(e.getResultCode(), e.getResultMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionHandler(RuntimeException e) {
        e.printStackTrace();
        return CommonResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerException(Exception e) {
        e.printStackTrace();
        return CommonResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR");
    }
}
