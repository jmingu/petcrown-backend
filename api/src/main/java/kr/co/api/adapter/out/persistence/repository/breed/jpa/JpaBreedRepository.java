package kr.co.api.adapter.out.persistence.repository.breed.jpa;

import kr.co.common.entity.breed.BreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBreedRepository extends JpaRepository<BreedEntity, Integer> {
}
