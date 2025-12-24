package kr.co.common.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.common.enums.CodeEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto {
    private int resultCode;
    private String resultMessageKo;
    private String resultMessageEn;
    @JsonInclude(JsonInclude.Include.ALWAYS)
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

    public CommonResponseDto(int resultCode, String resultMessageKo, String resultMessageEn, Object result) {
        this.resultCode = resultCode;
        this.resultMessageKo = resultMessageKo;
        this.resultMessageEn = resultMessageEn;
        this.result = result;
    }



    /**
     * 성공 응답 (데이터 없음)
     */
    public static <T> CommonResponseDto success(String message) {
        return new CommonResponseDto(200, message, message, null);
    }

    /**
     * 성공 응답 (데이터 있음)
     */
    public static <T> CommonResponseDto success(String message, T result) {
        return new CommonResponseDto(200, message, message, result);
    }

    /**
     * 실패 응답
     */
    public static <T> CommonResponseDto error(String message) {
        return new CommonResponseDto(400, message, message, null);
    }

    /**
     * 실패 응답 (코드 지정)
     */
    public static <T> CommonResponseDto error(int code, String message) {
        return new CommonResponseDto(code, message, message, null);
    }


}
