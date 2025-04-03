package kr.co.api.adapter.out.persistence.repository.standard.logintype.jpa;

import kr.co.common.entity.standard.logintype.LoginTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLoginTypeRepository extends JpaRepository<LoginTypeEntity, Integer> {
}
