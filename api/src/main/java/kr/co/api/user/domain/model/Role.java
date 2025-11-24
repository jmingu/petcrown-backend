package kr.co.api.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Role {
    private final Integer roleId;

    private final String roleName;

    private final Integer level;
    private final String isDefault;

    public static Role ofId(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        return new Role(roleId, null, null, null);
    }

    public static Role of(Integer roleId, String roleName, Integer level, String isDefault) {
        if (roleName == null) {

        }
        return new Role(null, null, null, null);
    }




}
