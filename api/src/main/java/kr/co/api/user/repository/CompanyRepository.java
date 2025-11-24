package kr.co.api.user.repository;

import kr.co.api.user.domain.model.Company;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static kr.co.common.jooq.Tables.COMPANY;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {

    private final DSLContext dsl;

    /**
     * 기본 회사 조회
     */
    public Company selectDefaultCompany() {
        return dsl.select()
                .from(COMPANY)
                .where(COMPANY.IS_DEFAULT.eq("Y"))
                .fetchOne(record-> Company.of(
                        record.get(COMPANY.COMPANY_ID),
                        record.get(COMPANY.COMPANY_NAME),
                        record.get(COMPANY.COMPANY_CODE),
                        record.get(COMPANY.ADDRESS),
                        record.get(COMPANY.PHONE_NUMBER),
                        record.get(COMPANY.IS_DEFAULT)
                ));
    }


}
