package kr.co.api.user.repository;

import kr.co.api.user.dto.command.EmailVerificationCodeDto;
import kr.co.common.entity.user.EmailVerificationEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepository {

    private final DSLContext dsl;

    /**
     * 이메일 인증 정보 저장
     */
    public void insertEmailVerification(EmailVerificationEntity emailVerification) {
        dsl.insertInto(EMAIL_VERIFICATION)
                .set(EMAIL_VERIFICATION.USER_ID, emailVerification.getUserId())
                .set(EMAIL_VERIFICATION.VERIFICATION_CODE, emailVerification.getVerificationCode())
                .set(EMAIL_VERIFICATION.EXPIRES_DATE, emailVerification.getExpiresDate())
                .set(EMAIL_VERIFICATION.CREATE_USER_ID, emailVerification.getCreateUserId())
                .set(EMAIL_VERIFICATION.UPDATE_USER_ID, emailVerification.getUpdateUserId())
                .execute();
    }

    /**
     * 사용자 Email로 인증코드 조회
     */
    public EmailVerificationCodeDto selectEmailCodeByEmail(String email) {
        var a = USER.as("a");
        var b = EMAIL_VERIFICATION.as("b");

        return dsl.select()
                .from(a)
                .innerJoin(b)
                .on(a.USER_ID.eq(b.USER_ID))
                .where(
                        a.EMAIL.eq(email)
                                .and(a.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToEmailVerificationCodeDto);
    }

    /**
     * 새로운 인증코드로 수정
     */
    public void updateVerificationNewCode(Long userId, String verificationCode, LocalDateTime expiresDate) {
        dsl.update(EMAIL_VERIFICATION)
                .set(EMAIL_VERIFICATION.VERIFICATION_CODE, verificationCode)
                .set(EMAIL_VERIFICATION.EXPIRES_DATE, expiresDate)
                .set(EMAIL_VERIFICATION.UPDATE_DATE, currentLocalDateTime())
                .set(EMAIL_VERIFICATION.UPDATE_USER_ID, userId)
                .where(EMAIL_VERIFICATION.USER_ID.eq(userId))
                .execute();
    }

    /**
     * Record를 EmailVerificationCodeDto로 변환
     */
    private EmailVerificationCodeDto mapToEmailVerificationCodeDto(Record record) {
        if (record == null) {
            return null;
        }

        return new EmailVerificationCodeDto(
                record.get(USER.USER_ID),
                record.get(USER.EMAIL),
                record.get(USER.IS_EMAIL_VERIFIED),
                record.get(EMAIL_VERIFICATION.EMAIL_VERIFICATION_ID),
                record.get(EMAIL_VERIFICATION.VERIFICATION_CODE),
                record.get(EMAIL_VERIFICATION.EXPIRES_DATE)
        );
    }
}
