package kr.co.api.adapter.out.persistence.repository.standard.ownership.jpa;

import kr.co.common.entity.standard.ownership.OwnershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOwnershipRepository extends JpaRepository<OwnershipEntity, Integer> {
}
