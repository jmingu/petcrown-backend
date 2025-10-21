package kr.co.common.exception;


import kr.co.common.enums.BusinessCode;
import kr.co.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PetCrownException extends RuntimeException {

    private int resultCode;
    private String resultMessageKo;
    private String resultMessageEn;

    public PetCrownException(CodeEnum codeEnum) {
        super(codeEnum.getMessageKo());
        this.resultCode = codeEnum.getCode();
        this.resultMessageKo = codeEnum.getMessageKo();
        this.resultMessageEn = codeEnum.getMessageEn();
    }



    public PetCrownException(BusinessCode businessCode) {
        super(businessCode.getMessageKo());
        this.resultCode = businessCode.getCode();
        this.resultMessageKo = businessCode.getMessageKo();
        this.resultMessageEn = businessCode.getMessageEn();

    }


    /**
     * INVALID_LENGTH에 사용
     * 길이 유효성 검증을 위해
     */
    public PetCrownException(BusinessCode businessCode, int min, int max) {
        super(businessCode.getMessageKo());
        this.resultCode = businessCode.getCode();
        this.resultMessageKo = businessCode.getFormattedMessageKo(min, max);
        this.resultMessageEn = businessCode.getFormattedMessageEn(min, max);
    }

    /**
     * INVALID_VALUE_LENGTH에 사용
     * 길이 유효성 검증을 위해
     */
    public PetCrownException(BusinessCode businessCode, String koName, String enName, int min, int max) {
        super(businessCode.getMessageKo());
        this.resultCode = businessCode.getCode();
        this.resultMessageKo = businessCode.getFormattedMessageKo(koName, min, max);
        this.resultMessageEn = businessCode.getFormattedMessageEn(enName, min, max);
    }


}
