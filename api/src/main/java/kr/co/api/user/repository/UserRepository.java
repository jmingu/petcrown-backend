package kr.co.api.user.repository;

import kr.co.api.user.dto.command.UserDeletionDto;
import kr.co.api.user.dto.command.UserUpdateDto;
import kr.co.common.entity.user.UserEntity;
import kr.co.common.jooq.enums.UserLoginTypeEnum;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.co.common.jooq.Tables.*;
import static kr.co.common.jooq.enums.RefTableEnum.*;
import static kr.co.common.jooq.enums.FileTypeEnum.*;
import static org.jooq.impl.DSL.*;
import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dsl;

    /**
     * 사용자 저장 (생성된 userId 반환)
     */
    public Long insertUser(UserEntity user) {
        return dsl.insertInto(USER)
                .set(USER.EMAIL, user.getEmail())
                .set(USER.USER_UUID, user.getUserUuid())
                .set(USER.PASSWORD, user.getPassword())
                .set(USER.ROLE_ID, user.getRoleId())
                .set(USER.NAME, user.getName())
                .set(USER.NICKNAME, user.getNickname())
                .set(USER.PHONE_NUMBER, user.getPhoneNumber())
                .set(USER.BIRTH_DATE, user.getBirthDate())
                .set(USER.GENDER, user.getGender())
                .set(USER.HEIGHT, user.getHeight() != null ? BigDecimal.valueOf(user.getHeight()) : null)
                .set(USER.WEIGHT, user.getWeight() != null ? BigDecimal.valueOf(user.getWeight()) : null)
                .set(USER.LOGIN_TYPE, user.getLoginType() != null ? UserLoginTypeEnum.valueOf(user.getLoginType()) : null)
                .set(USER.LOGIN_ID, user.getLoginId())
                .set(USER.IS_EMAIL_VERIFIED, user.getIsEmailVerified())
                .set(USER.COMPANY_ID, user.getCompanyId())
                .set(USER.DESCRIPTION, user.getDescription())
                .set(USER.CREATE_DATE, currentLocalDateTime())
                .set(USER.CREATE_USER_ID, user.getCreateUserId())
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, user.getUpdateUserId())
                .set(USER.IS_PHONE_NUMBER_VERIFIED, user.getIsPhoneNumberVerified())
                .returningResult(USER.USER_ID)  // 생성된 ID 반환
                .fetchOne()
                .getValue(USER.USER_ID);
    }

    /**
     * 닉네임으로 사용자 존재 여부 확인
     */
    public UserEntity selectByNickname(String nickname) {
        var u = USER.as("u");
        var f = FILE_INFO.as("f");

        return dsl.select()
                .from(u)
                .leftJoin(f)
                .on(
                        f.REF_TABLE.eq(user)
                                .and(f.REF_ID.eq(u.USER_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(
                        u.NICKNAME.eq(nickname)
                                .and(u.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToUserEntity);
    }

    /**
     * 이메일로 사용자 조회
     */
    public UserEntity selectByEmail(String email) {
        var u = USER.as("u");
        var f = FILE_INFO.as("f");

        return dsl.select()
                .from(u)
                .leftJoin(f)
                .on(
                        f.REF_TABLE.eq(user)
                                .and(f.REF_ID.eq(u.USER_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(
                        u.EMAIL.eq(email)
                                .and(u.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToUserEntity);
    }

    /**
     * 인증상태 업데이트
     */
    public void updateEmailVerificationStatus(Long userId) {
        dsl.update(USER)
                .set(USER.IS_EMAIL_VERIFIED, "Y")
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, userId)
                .where(USER.USER_ID.eq(userId))
                .execute();
    }

    /**
     * ID로 사용자 조회
     */
    public UserEntity selectByUserId(Long userId) {
        var u = USER.as("u");
        var f = FILE_INFO.as("f");

        return dsl.select()
                .from(u)
                .leftJoin(f)
                .on(
                        f.REF_TABLE.eq(user)
                                .and(f.REF_ID.eq(u.USER_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(
                        u.USER_ID.eq(userId)
                                .and(u.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToUserEntity);
    }

    /**
     * 사용자 정보 수정
     */
    public void updateUserInfo(UserUpdateDto userUpdateDto) {
        dsl.update(USER)
                .set(USER.NAME, userUpdateDto.getName())
                .set(USER.NICKNAME, userUpdateDto.getNickname())
                .set(USER.PHONE_NUMBER, userUpdateDto.getPhoneNumber())
                .set(USER.BIRTH_DATE, userUpdateDto.getBirthDate())
                .set(USER.GENDER, userUpdateDto.getGender())
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, userUpdateDto.getUserId())
                .where(USER.USER_ID.eq(userUpdateDto.getUserId())
                        .and(USER.DELETE_DATE.isNull()))
                .execute();
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(Long userId, String password) {
        dsl.update(USER)
                .set(USER.PASSWORD, password)
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, userId)
                .where(USER.USER_ID.eq(userId)
                        .and(USER.DELETE_DATE.isNull()))
                .execute();
    }

    /**
     * 이메일, 이름으로 사용자 조회 (비밀번호 찾기용)
     */
    public UserEntity selectByEmailAndName(String email, String name) {
        var u = USER.as("u");
        var f = FILE_INFO.as("f");

        return dsl.select()
                .from(u)
                .leftJoin(f)
                .on(
                        f.REF_TABLE.eq(user)
                                .and(f.REF_ID.eq(u.USER_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(
                        u.EMAIL.eq(email)
                                .and(u.NAME.eq(name))
                                .and(u.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToUserEntity);
    }

    /**
     * 사용자 삭제
     */
    public void deleteById(Long userId) {
        dsl.update(USER)
                .set(USER.DELETE_USER_ID, userId)
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .where(USER.USER_ID.eq(userId))
                .execute();
    }

    /**
     * 사용자 정보 검증 및 소프트 삭제 (userId, email, name, password 모두 일치해야 삭제)
     */
    public int softDeleteUser(UserDeletionDto userDeletionDto) {
        return dsl.update(USER)
                .set(USER.DELETE_DATE, currentLocalDateTime())
                .set(USER.DELETE_USER_ID, userDeletionDto.getUserId())
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, userDeletionDto.getUserId())
                .where(USER.USER_ID.eq(userDeletionDto.getUserId())
                        .and(USER.EMAIL.eq(userDeletionDto.getEmail()))
                        .and(USER.NAME.eq(userDeletionDto.getName()))
                        .and(USER.PASSWORD.eq(userDeletionDto.getPassword()))
                        .and(USER.DELETE_DATE.isNull()))
                .execute();
    }

    /**
     * Record를 UserEntity로 변환
     */
    private UserEntity mapToUserEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new UserEntity(
                record.get(USER.USER_ID),
                record.get(USER.EMAIL),
                record.get(USER.USER_UUID),
                record.get(USER.PASSWORD),
                record.get(USER.ROLE_ID),
                record.get(USER.NAME),
                record.get(USER.NICKNAME),
                record.get(USER.PHONE_NUMBER),
                record.get(USER.BIRTH_DATE),
                record.get(USER.GENDER),
                record.get(USER.HEIGHT) != null ? record.get(USER.HEIGHT).doubleValue() : null,
                record.get(USER.WEIGHT) != null ? record.get(USER.WEIGHT).doubleValue() : null,
                record.get(USER.LOGIN_TYPE) != null ? record.get(USER.LOGIN_TYPE).getLiteral() : null,
                record.get(USER.LOGIN_ID),
                record.get(USER.IS_EMAIL_VERIFIED),
                record.get(USER.IS_PHONE_NUMBER_VERIFIED),
                record.get(USER.COMPANY_ID),
                record.get(USER.DESCRIPTION),
                record.get(FILE_INFO.FILE_URL),
                record.get(USER.CREATE_DATE),
                record.get(USER.CREATE_USER_ID),
                record.get(USER.UPDATE_DATE),
                record.get(USER.UPDATE_USER_ID),
                record.get(USER.DELETE_DATE),
                record.get(USER.DELETE_USER_ID)
        );
    }
}
