package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDeletionDto {

    private final Long userId;
    private final String email;
    private final String name;
    private final String password;
}
