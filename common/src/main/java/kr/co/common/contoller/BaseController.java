package kr.co.common.contoller;

import kr.co.common.entity.common.CommonResponseDto;
import kr.co.common.enums.CodeEnum;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected ResponseEntity<CommonResponseDto> success() {
        return ResponseEntity.status(CodeEnum.SUCCESS.getCode())
                .body(new CommonResponseDto(CodeEnum.SUCCESS));
    }
    protected ResponseEntity<CommonResponseDto> success(Object data) {
        return ResponseEntity.status(CodeEnum.SUCCESS.getCode())
                .body(new CommonResponseDto(CodeEnum.SUCCESS, data));
    }

    protected ResponseEntity<CommonResponseDto> error() {
        return ResponseEntity.status(CodeEnum.ERROR.getCode())
                .body(new CommonResponseDto(CodeEnum.ERROR));
    }

    protected ResponseEntity<CommonResponseDto> error(int resultCode, String resultMessage) {
        return ResponseEntity.status(resultCode)
                .body(new CommonResponseDto(resultCode, resultMessage));
    }

    protected ResponseEntity<CommonResponseDto> error(CodeEnum codeEnum) {
        return ResponseEntity.status(codeEnum.getCode())
                .body(new CommonResponseDto(codeEnum));
    }



}