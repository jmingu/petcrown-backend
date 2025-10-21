package kr.co.api.user.domain.vo;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter

public class Nickname {

    private final String value;

    private Nickname(String nickname) {
        ValidationUtils.validateNameString(nickname, "닉네임", "nickname", 2, 10);
        this.value = nickname;
    }

    public static Nickname of(String nickname) {
        return new Nickname(nickname);
    }


}