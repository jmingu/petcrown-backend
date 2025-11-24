package kr.co.api.user.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.ValidationUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Password {

    private final String value;

    private Password(String password) {
        this.value = password;
    }

    private Password(String password, String passwordCheck) {
        log.debug("Creating Password with check: password={}, passwordCheck={}", password.length(), passwordCheck);
        ValidationUtils.validateNameString(password, "비밀번호", "password", 4, 20);
        ValidationUtils.validateNameString(passwordCheck, "비밀번호 확인", "password check", 4, 20);

        if (!password.equals(passwordCheck)) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD);
        }
        this.value = password;
    }

    public static Password from(String password) {
        return new Password(password);
    }

    /**
     * 비밀번호와 비밀번호 확인 값 필요 시
     * ex: 회원가입, 비밀번호 변경 등
     */
    public static Password createPasswordCheck (String password, String passwordCheck) {

        return new Password(password, passwordCheck);
    }

}

