package kr.co.api.user.repository;

import kr.co.api.user.domain.model.User;
import kr.co.api.user.dto.command.UserDeletionDto;
import kr.co.api.user.dto.command.UserInfoDto;
import kr.co.api.user.dto.command.UserUpdateDto;
import kr.co.common.jooq.enums.UserLoginTypeEnum;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static kr.co.common.jooq.Tables.FILE_INFO;
import static kr.co.common.jooq.Tables.USER;
import static kr.co.common.jooq.enums.FileTypeEnum.IMAGE;
import static kr.co.common.jooq.enums.RefTableEnum.user;
import static org.jooq.impl.DSL.currentLocalDateTime;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dsl;

    /**
     * 사용자 저장 (생성된 userId 반환)
     */
    public User insertUser(User user) {
        Long generatedId =  dsl.insertInto(USER)
                .set(USER.EMAIL, user.getEmail().getValue())
                .set(USER.USER_UUID, user.getUserUuid())
                .set(USER.PASSWORD, user.getPassword().getValue())
                .set(USER.ROLE_ID, user.getRole().getRoleId())
                .set(USER.NAME, user.getName().getValue())
                .set(USER.NICKNAME, user.getNickname().getValue())
                .set(USER.PHONE_NUMBER, user.getPhoneNumber() == null ? null : user.getPhoneNumber().getValue())
                .set(USER.BIRTH_DATE, user.getBirthDate())
                .set(USER.GENDER, user.getGender() == null ? null : user.getGender().getValue())
                .set(USER.HEIGHT, user.getHeight() != null ? BigDecimal.valueOf(user.getHeight()) : null)
                .set(USER.WEIGHT, user.getWeight() != null ? BigDecimal.valueOf(user.getWeight()) : null)
                .set(USER.LOGIN_TYPE, user.getLoginType() != null ? UserLoginTypeEnum.valueOf(user.getLoginType()) : null)
                .set(USER.LOGIN_ID, user.getLoginId())
                .set(USER.IS_EMAIL_VERIFIED, user.getIsEmailVerified())
                .set(USER.COMPANY_ID, user.getCompany().getCompanyId())
                .set(USER.DESCRIPTION, user.getDescription())
                .set(USER.CREATE_DATE, currentLocalDateTime())
                .set(USER.CREATE_USER_ID, (Long) null)
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, (Long) null)
                .set(USER.IS_PHONE_NUMBER_VERIFIED, user.getIsPhoneNumberVerified())
                .returningResult(USER.USER_ID)  // 생성된 ID 반환
                .fetchOne()
                .getValue(USER.USER_ID);

        // 생성된 ID를 Domain 객체에 반영
        return user.withUserId(generatedId);

    }

    /**
     * 닉네임으로 사용자 존재 여부 확인
     */
    public UserInfoDto selectByNickname(String nickname) {
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
                .fetchOne(record -> new UserInfoDto(
                        record.get(USER.USER_ID),
                        record.get(USER.EMAIL),
                        record.get(USER.PASSWORD),
                        record.get(USER.NAME),
                        record.get(USER.NICKNAME),
                        record.get(USER.PHONE_NUMBER),
                        record.get(FILE_INFO.FILE_URL),
                        record.get(USER.BIRTH_DATE),
                        record.get(USER.GENDER),
                        record.get(USER.IS_EMAIL_VERIFIED)
                ));
    }

    /**
     * 이메일로 사용자 조회
     */
    public UserInfoDto selectByEmail(String email) {
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
                .fetchOne(record -> new UserInfoDto(
                        record.get(USER.USER_ID),
                        record.get(USER.EMAIL),
                        record.get(USER.PASSWORD),
                        record.get(USER.NAME),
                        record.get(USER.NICKNAME),
                        record.get(USER.PHONE_NUMBER),
                        record.get(FILE_INFO.FILE_URL),
                        record.get(USER.BIRTH_DATE),
                        record.get(USER.GENDER),
                        record.get(USER.IS_EMAIL_VERIFIED)
                ));
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
    public UserInfoDto selectByUserId(Long userId) {
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
                .fetchOne(record -> new UserInfoDto(
                        record.get(USER.USER_ID),
                        record.get(USER.EMAIL),
                        record.get(USER.PASSWORD),
                        record.get(USER.NAME),
                        record.get(USER.NICKNAME),
                        record.get(USER.PHONE_NUMBER),
                        record.get(FILE_INFO.FILE_URL),
                        record.get(USER.BIRTH_DATE),
                        record.get(USER.GENDER),
                        record.get(USER.IS_EMAIL_VERIFIED)
                ));
    }

    /**
     * 사용자 정보 수정 - 도메인으로 파라미터 받기
     */
    public void updateUserInfo(User user) {
        dsl.update(USER)
                .set(USER.NAME, user.getName().getValue())
                .set(USER.NICKNAME, user.getNickname().getValue())
                .set(USER.PHONE_NUMBER, user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null)
                .set(USER.BIRTH_DATE, user.getBirthDate())
                .set(USER.GENDER, user.getGender() != null ? user.getGender().getValue() : null)
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, user.getUserId())
                .where(USER.USER_ID.eq(user.getUserId())
                        .and(USER.DELETE_DATE.isNull()))
                .execute();
    }

    /**
     * 비밀번호 변경 - 도메인으로 파라미터 받기
     */
    public void updatePassword(User user) {
        dsl.update(USER)
                .set(USER.PASSWORD, user.getPassword().getValue())
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, user.getUserId())
                .where(USER.USER_ID.eq(user.getUserId())
                        .and(USER.DELETE_DATE.isNull()))
                .execute();
    }

    /**
     * 이메일, 이름으로 사용자 조회 (비밀번호 찾기용)
     */
    public UserInfoDto selectByEmailAndName(String email, String name) {
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
                .fetchOne(record -> new UserInfoDto(
                        record.get(USER.USER_ID),
                        record.get(USER.EMAIL),
                        record.get(USER.PASSWORD),
                        record.get(USER.NAME),
                        record.get(USER.NICKNAME),
                        record.get(USER.PHONE_NUMBER),
                        record.get(FILE_INFO.FILE_URL),
                        record.get(USER.BIRTH_DATE),
                        record.get(USER.GENDER),
                        record.get(USER.IS_EMAIL_VERIFIED)
                ));
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
     * 사용자 정보 검증 및 소프트 삭제 - 도메인으로 파라미터 받기
     * (userId, email, name, password 모두 일치해야 삭제)
     */
    public int softDeleteUser(User user) {
        return dsl.update(USER)
                .set(USER.DELETE_DATE, currentLocalDateTime())
                .set(USER.DELETE_USER_ID, user.getUserId())
                .set(USER.UPDATE_DATE, currentLocalDateTime())
                .set(USER.UPDATE_USER_ID, user.getUserId())
                .where(USER.USER_ID.eq(user.getUserId())
                        .and(USER.EMAIL.eq(user.getEmail().getValue()))
                        .and(USER.NAME.eq(user.getName().getValue()))
                        .and(USER.PASSWORD.eq(user.getPassword().getValue()))
                        .and(USER.DELETE_DATE.isNull()))
                .execute();
    }

}
