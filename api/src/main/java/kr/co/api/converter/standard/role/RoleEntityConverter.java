package kr.co.api.converter.standard.role;

import kr.co.api.domain.model.standard.role.Role;
import kr.co.common.entity.standard.role.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleEntityConverter {
    public RoleEntity toEntity(Role role) {
        return new RoleEntity(role.getRoleId(), role.getRoleName(), role.getLevel());
    }

    public Role toDomain(RoleEntity entity) {
        return new Role(entity.getRoleId(), entity.getRoleName(), entity.getLevel());
    }
}
