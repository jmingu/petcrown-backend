package kr.co.api.user.repository;

import kr.co.common.entity.user.EmailGuestEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class EmailGuestRepository {

    private final DSLContext dsl;

    /**
     * 오늘 날짜에 인증된 이메일인지 확인 (DB의 current_date 사용)
     */
    public EmailGuestEntity selectTodayVerifiedEmail(String email) {
        return dsl.select()
                .from(EMAIL_GUEST)
                .where(
                        EMAIL_GUEST.EMAIL.eq(email)
                                .and(EMAIL_GUEST.JOIN_DATE.eq(currentDate().cast(LocalDate.class)))  // DB 기준 날짜 사용
                )
                .fetchOne(this::mapToEmailGuestEntity);
    }

    /**
     * 이메일 게스트 저장 (생성된 emailGuestId 반환, DB의 current_date/current_timestamp 사용)
     */
    public Long insertEmailGuest(EmailGuestEntity emailGuestEntity) {
        return dsl.insertInto(EMAIL_GUEST)
                .set(EMAIL_GUEST.EMAIL, emailGuestEntity.getEmail())
                .set(EMAIL_GUEST.JOIN_DATE, currentDate().cast(LocalDate.class))  // DB 기준 날짜 사용
                .set(EMAIL_GUEST.ENCRYPTE_TOKEN, emailGuestEntity.getEncrypteToken())
                .set(EMAIL_GUEST.CREATE_DATE, currentLocalDateTime())  // DB 기준 시간 사용
                .returningResult(EMAIL_GUEST.EMAIL_GUEST_ID)
                .fetchOne()
                .getValue(EMAIL_GUEST.EMAIL_GUEST_ID);
    }

    /**
     * Record를 EmailGuestEntity로 변환
     */
    private EmailGuestEntity mapToEmailGuestEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new EmailGuestEntity(
                record.get(EMAIL_GUEST.EMAIL_GUEST_ID),
                record.get(EMAIL_GUEST.EMAIL),
                record.get(EMAIL_GUEST.JOIN_DATE),
                record.get(EMAIL_GUEST.ENCRYPTE_TOKEN),
                record.get(EMAIL_GUEST.CREATE_DATE)
        );
    }
}
