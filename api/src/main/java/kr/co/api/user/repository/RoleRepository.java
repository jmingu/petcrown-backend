package kr.co.api.user.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Role;
import org.springframework.stereotype.Repository;

import static kr.co.common.jooq.Tables.ROLE;

@Repository
@RequiredArgsConstructor
public class RoleRepository {

    private final DSLContext dsl;

    /**
     * 기본 역할 조회
     */
    public kr.co.api.user.domain.model.Role selectDefaultRole() {
        return dsl.select()
                .from(ROLE)
                .where(ROLE.IS_DEFAULT.eq("Y"))
                .fetchOne(record-> kr.co.api.user.domain.model.Role.of(
                        record.get(ROLE.ROLE_ID),
                        record.get(ROLE.ROLE_NAME),
                        record.get(ROLE.LEVEL),
                        record.get(ROLE.IS_DEFAULT)
                ));
    }


}
