package kr.co.api.user.domain.vo;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter
public class UserName {
    
    private final String value;
    
    private UserName(String name) {
        this.value = name;
    }

    public static UserName of(String name) {
        ValidationUtils.validateNameString(name, "이름", "name",1, 10);
        return new UserName(name);
    }

    public static UserName from(String name) {
        return new UserName(name); // 검증 없음
    }

}