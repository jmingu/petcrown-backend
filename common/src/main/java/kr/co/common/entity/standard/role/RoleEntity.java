package kr.co.common.entity.standard.role;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoleEntity extends BaseEntity {
    @Id
    private Integer roleId;  // 기본 PK

    private String roleName; // USER, ADMIN 등

    private Integer level;

}
