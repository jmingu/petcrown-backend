package kr.co.api.notice.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

@Getter
public class ContentType {

    private final String value;

    private ContentType(String contentType) {
        if (contentType == null || (!contentType.equals("TEXT") && !contentType.equals("HTML"))) {
            throw new PetCrownException(BusinessCode.INVALID_FORMAT);
        }
        this.value = contentType;
    }

    public static ContentType of(String contentType) {
        return new ContentType(contentType);
    }

    public static ContentType text() {
        return new ContentType("TEXT");
    }

    public static ContentType html() {
        return new ContentType("HTML");
    }
}