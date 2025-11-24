package kr.co.api.user.domain.vo;

import kr.co.common.util.ValidationUtils;
import lombok.Getter;

@Getter

public class Nickname {

    private final String value;

    private Nickname(String nickname) {

        this.value = nickname;
    }

    public static Nickname of(String nickname) {
        ValidationUtils.validateNameString(nickname, "닉네임", "nickname", 2, 10);
        return new Nickname(nickname);
    }

    public static Nickname from(String nickname) {
        return new Nickname(nickname);
    }


}