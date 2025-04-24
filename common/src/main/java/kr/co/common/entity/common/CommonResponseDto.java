package kr.co.common.entity.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto {
    private int resultCode;
    private String resultMessageKo;
    private String resultMessageEn;
    private Object result;

    public CommonResponseDto(CodeEnum codeEnum, Object result) {
        this.resultCode = codeEnum.getCode();
        this.resultMessageKo = codeEnum.getMessageKo();
        this.resultMessageEn = codeEnum.getMessageEn();
        this.result = result;
    }
    public CommonResponseDto(CodeEnum codeEnum) {
        this.resultCode = codeEnum.getCode();
        this.resultMessageKo = codeEnum.getMessageKo();
        this.resultMessageEn = codeEnum.getMessageEn();
    }

    public CommonResponseDto(int resultCode, String resultMessageKo, String resultMessageEn) {
        this.resultCode = resultCode;
        this.resultMessageKo = resultMessageKo;
        this.resultMessageEn = resultMessageEn;
    }
    public CommonResponseDto(int resultCode, String resultMessageKo, String resultMessageEn,  Object result) {
        this.resultCode = resultCode;
        this.resultMessageKo = resultMessageKo;
        this.resultMessageEn = resultMessageEn;
        this.result = result;
    }



//    public static <T> ResponseEntity<CommonResponseDto<T>> success(T result) {
//        return ResponseEntity.status(CodeEnum.SUCCESS.getCode()).body(new CommonResponseDto<>(result));
//    }
//
//
//    public static ResponseEntity<CommonResponseDto> error() {
//        return ResponseEntity.status(CodeEnum.ERROR.getCode()).body(new CommonResponseDto<>(CodeEnum.ERROR));
//    }
//
//    public static ResponseEntity<CommonResponseDto> error(int resultCode, String resultMessage) {
//        return ResponseEntity.status(resultCode).body(new CommonResponseDto(resultCode, resultMessage));
//    }
//
//    public static ResponseEntity<CommonResponseDto> error(CodeEnum codeEnum) {
//        return ResponseEntity.status(codeEnum.getCode()).body(new CommonResponseDto<>(codeEnum));
//    }


}
