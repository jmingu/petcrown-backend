package kr.co.api.adapter.out.persistence.repository.standard.role.jpa;

import kr.co.common.entity.standard.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleRepository extends JpaRepository<RoleEntity, Integer> {
}
