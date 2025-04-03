package kr.co.api.adapter.out.persistence.repository.standard.species.jpa;

import kr.co.common.entity.standard.role.RoleEntity;
import kr.co.common.entity.standard.species.SpeciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSpeciesRepository extends JpaRepository<SpeciesEntity, Integer> {
}
