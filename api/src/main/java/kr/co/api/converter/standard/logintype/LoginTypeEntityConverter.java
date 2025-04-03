package kr.co.api.converter.standard.logintype;

import kr.co.api.domain.model.standard.logintype.LoginType;
import kr.co.common.entity.standard.logintype.LoginTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class LoginTypeEntityConverter {
    public LoginTypeEntity toEntity(LoginType loginType) {
        return new LoginTypeEntity(loginType.getLoginTypeId(), loginType.getTypeName());
    }

    public LoginType toDomain(LoginTypeEntity entity) {
        return new LoginType(entity.getLoginTypeId(), entity.getTypeName());
    }
}
