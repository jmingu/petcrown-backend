package kr.co.api.notice.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

@Getter
public class Content {

    private final String value;

    private Content(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        this.value = content.trim();
    }

    public static Content of(String content) {
        return new Content(content);
    }
}