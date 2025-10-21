package kr.co.api.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Role {
    private Integer roleId;

    private String roleName;

    private Integer level;

    /**
     * RoleEntity로부터 Role 도메인 생성
     */
    public static Role ofId(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        return new Role(roleId, null, null);
    }

    /**
     * role
     */

}
