package kr.co.common.exception;


import kr.co.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class petCrownException extends RuntimeException {

    private int resultCode;
    private String resultMessage;

    public petCrownException(CodeEnum codeEnum) {
        this.resultCode = codeEnum.getCode();
        this.resultMessage = null;
    }

    public petCrownException(String resultMessage) {
        super(resultMessage);
        this.resultCode = CodeEnum.ERROR.getCode();
        this.resultMessage = resultMessage;
    }

    public petCrownException(CodeEnum codeEnum, String resultMessage) {
        super(resultMessage);
        this.resultCode = codeEnum.getCode();
        this.resultMessage = resultMessage;
    }

}
