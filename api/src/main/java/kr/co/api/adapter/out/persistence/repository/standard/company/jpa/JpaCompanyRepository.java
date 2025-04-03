package kr.co.api.adapter.out.persistence.repository.standard.company.jpa;

import kr.co.common.entity.standard.company.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCompanyRepository extends JpaRepository<CompanyEntity, Integer> {
}
