package kr.co.api.domain.model.standard.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Role {
    private Integer roleId;

    private String roleName;

    private Integer level;

}
