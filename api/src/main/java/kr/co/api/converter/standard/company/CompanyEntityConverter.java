package kr.co.api.converter.standard.company;

import kr.co.api.domain.model.standard.company.Company;
import kr.co.common.entity.standard.company.CompanyEntity;
import org.springframework.stereotype.Component;

@Component
public class CompanyEntityConverter {
    public CompanyEntity toEntity(Company company) {
        return new CompanyEntity(company.getCompanyId(), company.getCompanyName(), company.getCompanyCode(), company.getAddress(), company.getPhoneNumber());
    }

    public Company toDomain(CompanyEntity entity) {
        return new Company(entity.getCompanyId(), entity.getCompanyName(), entity.getCompanyCode(), entity.getCompanyCode(), entity.getPhoneNumber());
    }
}
