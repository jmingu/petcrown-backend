package kr.co.api.user.domain.vo;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter
public class UserName {
    
    private final String value;
    
    private UserName(String name) {
        ValidationUtils.validateNameString(name, "이름", "name",2, 10);
        this.value = name;
    }

    public static UserName of(String name) {
        return new UserName(name);
    }
    

}