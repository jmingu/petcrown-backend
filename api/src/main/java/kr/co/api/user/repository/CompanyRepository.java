package kr.co.api.user.repository;

import kr.co.common.entity.standard.company.CompanyEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import static kr.co.common.jooq.Tables.*;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {

    private final DSLContext dsl;

    /**
     * 기본 회사 조회
     */
    public CompanyEntity selectDefaultCompany() {
        return dsl.select()
                .from(COMPANY)
                .where(COMPANY.IS_DEFAULT.eq("Y"))
                .fetchOne(this::mapToCompanyEntity);
    }

    /**
     * Record를 CompanyEntity로 변환
     */
    private CompanyEntity mapToCompanyEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new CompanyEntity(
                record.get(COMPANY.COMPANY_ID),
                record.get(COMPANY.COMPANY_NAME),
                record.get(COMPANY.COMPANY_CODE),
                record.get(COMPANY.ADDRESS),
                record.get(COMPANY.PHONE_NUMBER),
                record.get(COMPANY.IS_DEFAULT),
                record.get(COMPANY.CREATE_DATE),
                record.get(COMPANY.CREATE_USER_ID),
                record.get(COMPANY.UPDATE_DATE),
                record.get(COMPANY.UPDATE_USER_ID),
                record.get(COMPANY.DELETE_DATE),
                record.get(COMPANY.DELETE_USER_ID)
        );
    }
}
