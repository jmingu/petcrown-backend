package kr.co.api.user.repository;

import kr.co.common.entity.standard.role.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import static kr.co.common.jooq.Tables.*;

@Repository
@RequiredArgsConstructor
public class RoleRepository {

    private final DSLContext dsl;

    /**
     * 기본 역할 조회
     */
    public RoleEntity selectDefaultRole() {
        return dsl.select()
                .from(ROLE)
                .where(ROLE.IS_DEFAULT.eq("Y"))
                .fetchOne(this::mapToRoleEntity);
    }

    /**
     * Record를 RoleEntity로 변환
     */
    private RoleEntity mapToRoleEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new RoleEntity(
                record.get(ROLE.ROLE_ID),
                record.get(ROLE.ROLE_NAME),
                record.get(ROLE.LEVEL),
                record.get(ROLE.IS_DEFAULT),
                record.get(ROLE.CREATE_DATE),
                record.get(ROLE.CREATE_USER_ID),
                record.get(ROLE.UPDATE_DATE),
                record.get(ROLE.UPDATE_USER_ID),
                record.get(ROLE.DELETE_DATE),
                record.get(ROLE.DELETE_USER_ID)
        );
    }
}
