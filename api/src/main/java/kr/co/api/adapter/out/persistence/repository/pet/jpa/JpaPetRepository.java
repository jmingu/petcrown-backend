package kr.co.api.adapter.out.persistence.repository.pet.jpa;

import kr.co.common.entity.pet.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPetRepository extends JpaRepository<PetEntity, Long> {
}
