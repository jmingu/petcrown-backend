package kr.co.api.event.repository;

import kr.co.api.event.dto.command.EventUpdateDto;
import kr.co.common.entity.event.EventEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final DSLContext dsl;

    /**
     * 이벤트 저장 (생성된 eventId 반환)
     */
    public Long insertEvent(EventEntity event) {
        return dsl.insertInto(EVENT)
                .set(EVENT.TITLE, event.getTitle())
                .set(EVENT.CONTENT, event.getContent())
                .set(EVENT.CONTENT_TYPE, event.getContentType())
                .set(EVENT.START_DATE, event.getStartDate())
                .set(EVENT.END_DATE, event.getEndDate())
                .set(EVENT.VIEW_COUNT, event.getViewCount())
                .set(EVENT.CREATE_DATE, currentLocalDateTime())
                .set(EVENT.CREATE_USER_ID, event.getCreateUserId())
                .set(EVENT.UPDATED_DATE, currentLocalDateTime())
                .set(EVENT.UPDATE_USER_ID, event.getCreateUserId())
                .returningResult(EVENT.EVENT_ID)
                .fetchOne()
                .getValue(EVENT.EVENT_ID);
    }

    /**
     * 이벤트 ID로 조회
     */
    public EventEntity selectByEventId(Long eventId) {
        return dsl.select()
                .from(EVENT)
                .where(
                        EVENT.EVENT_ID.eq(eventId)
                                .and(EVENT.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToEventEntity);
    }

    /**
     * 활성화된 이벤트 목록 조회 (페이징)
     */
    public List<EventEntity> selectActiveEvents(int offset, int limit) {
        return dsl.select()
                .from(EVENT)
                .where(
                        EVENT.DELETE_DATE.isNull()
                                .and(EVENT.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        EVENT.END_DATE.isNull()
                                                .or(EVENT.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .orderBy(EVENT.CREATE_DATE.desc())
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToEventEntity);
    }

    /**
     * 전체 이벤트 목록 조회 (관리자용)
     */
    public List<EventEntity> selectAllEvents(int offset, int limit) {
        return dsl.select()
                .from(EVENT)
                .where(EVENT.DELETE_DATE.isNull())
                .orderBy(EVENT.CREATE_DATE.desc())
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToEventEntity);
    }

    /**
     * 활성화된 이벤트 개수 조회
     */
    public int countActiveEvents() {
        Integer count = dsl.selectCount()
                .from(EVENT)
                .where(
                        EVENT.DELETE_DATE.isNull()
                                .and(EVENT.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        EVENT.END_DATE.isNull()
                                                .or(EVENT.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 전체 이벤트 개수 조회
     */
    public int countAllEvents() {
        Integer count = dsl.selectCount()
                .from(EVENT)
                .where(EVENT.DELETE_DATE.isNull())
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 이벤트 수정
     */
    public void updateEvent(EventUpdateDto eventUpdateDto) {
        dsl.update(EVENT)
                .set(EVENT.TITLE, eventUpdateDto.getTitle())
                .set(EVENT.CONTENT, eventUpdateDto.getContent())
                .set(EVENT.CONTENT_TYPE, eventUpdateDto.getContentType())
                .set(EVENT.START_DATE, eventUpdateDto.getStartDate())
                .set(EVENT.END_DATE, eventUpdateDto.getEndDate())
                .set(EVENT.UPDATED_DATE, currentLocalDateTime())
                .set(EVENT.UPDATE_USER_ID, eventUpdateDto.getUpdateUserId())
                .where(
                        EVENT.EVENT_ID.eq(eventUpdateDto.getEventId())
                                .and(EVENT.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(Long eventId) {
        dsl.update(EVENT)
                .set(EVENT.VIEW_COUNT, EVENT.VIEW_COUNT.plus(1))
                .set(EVENT.UPDATED_DATE, currentLocalDateTime())
                .where(
                        EVENT.EVENT_ID.eq(eventId)
                                .and(EVENT.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 이벤트 삭제 (논리 삭제)
     */
    public void deleteById(Long eventId, Long deleteUserId) {
        dsl.update(EVENT)
                .set(EVENT.DELETE_DATE, currentLocalDateTime())
                .set(EVENT.DELETE_USER_ID, deleteUserId)
                .where(EVENT.EVENT_ID.eq(eventId))
                .execute();
    }

    /**
     * 제목으로 이벤트 검색
     */
    public List<EventEntity> searchByTitle(String title, int offset, int limit) {
        return dsl.select()
                .from(EVENT)
                .where(
                        EVENT.DELETE_DATE.isNull()
                                .and(lower(EVENT.TITLE).like(lower("%" + title + "%")))
                                .and(EVENT.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        EVENT.END_DATE.isNull()
                                                .or(EVENT.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .orderBy(EVENT.CREATE_DATE.desc())
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToEventEntity);
    }

    /**
     * 제목으로 검색된 이벤트 개수
     */
    public int countSearchByTitle(String title) {
        Integer count = dsl.selectCount()
                .from(EVENT)
                .where(
                        EVENT.DELETE_DATE.isNull()
                                .and(lower(EVENT.TITLE).like(lower("%" + title + "%")))
                                .and(EVENT.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        EVENT.END_DATE.isNull()
                                                .or(EVENT.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Record를 EventEntity로 변환
     */
    private EventEntity mapToEventEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new EventEntity(
                record.get(EVENT.EVENT_ID),
                record.get(EVENT.TITLE),
                record.get(EVENT.CONTENT),
                record.get(EVENT.CONTENT_TYPE),
                record.get(EVENT.START_DATE),
                record.get(EVENT.END_DATE),
                record.get(EVENT.VIEW_COUNT),
                record.get(EVENT.CREATE_DATE),
                record.get(EVENT.CREATE_USER_ID),
                record.get(EVENT.UPDATED_DATE),
                record.get(EVENT.UPDATE_USER_ID),
                record.get(EVENT.DELETE_DATE),
                record.get(EVENT.DELETE_USER_ID)
        );
    }
}
