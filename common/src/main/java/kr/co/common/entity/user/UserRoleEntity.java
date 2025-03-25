package kr.co.common.entity.user;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRoleEntity extends BaseEntity {
    @Id
    private Integer roleId;  // 기본 PK

    private String roleName; // USER, ADMIN 등

    private Integer level;

}
