package kr.co.api.notice.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

@Getter
public class Title {

    private final String value;

    private Title(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (title.length() > 255) {
            throw new PetCrownException(BusinessCode.INVALID_LENGTH, 0, 255);
        }
        this.value = title.trim();
    }

    public static Title of(String title) {
        return new Title(title);
    }
}