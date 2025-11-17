package kr.co.api.notice.repository;

import kr.co.api.notice.dto.command.NoticeUpdateDto;
import kr.co.common.entity.notice.NoticeEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {

    private final DSLContext dsl;

    /**
     * 공지사항 저장 (생성된 noticeId 반환)
     */
    public Long insertNotice(NoticeEntity notice) {
        return dsl.insertInto(NOTICE)
                .set(NOTICE.TITLE, notice.getTitle())
                .set(NOTICE.CONTENT, notice.getContent())
                .set(NOTICE.CONTENT_TYPE, notice.getContentType())
                .set(NOTICE.IS_PINNED, notice.getIsPinned())
                .set(NOTICE.PIN_ORDER, notice.getPinOrder())
                .set(NOTICE.START_DATE, notice.getStartDate())
                .set(NOTICE.END_DATE, notice.getEndDate())
                .set(NOTICE.VIEW_COUNT, notice.getViewCount())
                .set(NOTICE.CREATE_DATE, currentLocalDateTime())
                .set(NOTICE.CREATE_USER_ID, notice.getCreateUserId())
                .set(NOTICE.UPDATED_DATE, currentLocalDateTime())
                .set(NOTICE.UPDATE_USER_ID, notice.getCreateUserId())
                .returningResult(NOTICE.NOTICE_ID)
                .fetchOne()
                .getValue(NOTICE.NOTICE_ID);
    }

    /**
     * 공지사항 ID로 조회
     */
    public NoticeEntity selectByNoticeId(Long noticeId) {
        return dsl.select()
                .from(NOTICE)
                .where(
                        NOTICE.NOTICE_ID.eq(noticeId)
                                .and(NOTICE.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToNoticeEntity);
    }

    /**
     * 활성화된 공지사항 목록 조회 (페이징)
     */
    public List<NoticeEntity> selectActiveNotices(int offset, int limit) {
        return dsl.select()
                .from(NOTICE)
                .where(
                        NOTICE.DELETE_DATE.isNull()
                                .and(NOTICE.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        NOTICE.END_DATE.isNull()
                                                .or(NOTICE.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .orderBy(
                        NOTICE.IS_PINNED.desc(),
                        NOTICE.PIN_ORDER.asc(),
                        NOTICE.CREATE_DATE.desc()
                )
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToNoticeEntity);
    }

    /**
     * 상단 고정 공지사항 목록 조회
     */
    public List<NoticeEntity> selectPinnedNotices() {
        return dsl.select()
                .from(NOTICE)
                .where(
                        NOTICE.DELETE_DATE.isNull()
                                .and(NOTICE.IS_PINNED.eq("Y"))
                                .and(NOTICE.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        NOTICE.END_DATE.isNull()
                                                .or(NOTICE.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .orderBy(
                        NOTICE.PIN_ORDER.asc(),
                        NOTICE.CREATE_DATE.desc()
                )
                .fetch(this::mapToNoticeEntity);
    }

    /**
     * 전체 공지사항 목록 조회 (관리자용)
     */
    public List<NoticeEntity> selectAllNotices(int offset, int limit) {
        return dsl.select()
                .from(NOTICE)
                .where(NOTICE.DELETE_DATE.isNull())
                .orderBy(
                        NOTICE.IS_PINNED.desc(),
                        NOTICE.PIN_ORDER.asc(),
                        NOTICE.CREATE_DATE.desc()
                )
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToNoticeEntity);
    }

    /**
     * 활성화된 공지사항 개수 조회
     */
    public int countActiveNotices() {
        Integer count = dsl.selectCount()
                .from(NOTICE)
                .where(
                        NOTICE.DELETE_DATE.isNull()
                                .and(NOTICE.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        NOTICE.END_DATE.isNull()
                                                .or(NOTICE.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 전체 공지사항 개수 조회
     */
    public int countAllNotices() {
        Integer count = dsl.selectCount()
                .from(NOTICE)
                .where(NOTICE.DELETE_DATE.isNull())
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 공지사항 수정
     */
    public void updateNotice(NoticeUpdateDto noticeUpdateDto) {
        dsl.update(NOTICE)
                .set(NOTICE.TITLE, noticeUpdateDto.getTitle())
                .set(NOTICE.CONTENT, noticeUpdateDto.getContent())
                .set(NOTICE.CONTENT_TYPE, noticeUpdateDto.getContentType())
                .set(NOTICE.IS_PINNED, noticeUpdateDto.getIsPinned())
                .set(NOTICE.PIN_ORDER, noticeUpdateDto.getPinOrder())
                .set(NOTICE.START_DATE, noticeUpdateDto.getStartDate())
                .set(NOTICE.END_DATE, noticeUpdateDto.getEndDate())
                .set(NOTICE.UPDATED_DATE, currentLocalDateTime())
                .set(NOTICE.UPDATE_USER_ID, noticeUpdateDto.getUpdateUserId())
                .where(
                        NOTICE.NOTICE_ID.eq(noticeUpdateDto.getNoticeId())
                                .and(NOTICE.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(Long noticeId) {
        dsl.update(NOTICE)
                .set(NOTICE.VIEW_COUNT, NOTICE.VIEW_COUNT.plus(1))
                .set(NOTICE.UPDATED_DATE, currentLocalDateTime())
                .where(
                        NOTICE.NOTICE_ID.eq(noticeId)
                                .and(NOTICE.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 공지사항 삭제 (논리 삭제)
     */
    public void deleteById(Long noticeId, Long deleteUserId) {
        dsl.update(NOTICE)
                .set(NOTICE.DELETE_DATE, currentLocalDateTime())
                .set(NOTICE.DELETE_USER_ID, deleteUserId)
                .where(NOTICE.NOTICE_ID.eq(noticeId))
                .execute();
    }

    /**
     * 제목으로 공지사항 검색
     */
    public List<NoticeEntity> searchByTitle(String title, int offset, int limit) {
        return dsl.select()
                .from(NOTICE)
                .where(
                        NOTICE.DELETE_DATE.isNull()
                                .and(lower(NOTICE.TITLE).like(lower("%" + title + "%")))
                                .and(NOTICE.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        NOTICE.END_DATE.isNull()
                                                .or(NOTICE.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .orderBy(
                        NOTICE.IS_PINNED.desc(),
                        NOTICE.PIN_ORDER.asc(),
                        NOTICE.CREATE_DATE.desc()
                )
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToNoticeEntity);
    }

    /**
     * 제목으로 검색된 공지사항 개수
     */
    public int countSearchByTitle(String title) {
        Integer count = dsl.selectCount()
                .from(NOTICE)
                .where(
                        NOTICE.DELETE_DATE.isNull()
                                .and(lower(NOTICE.TITLE).like(lower("%" + title + "%")))
                                .and(NOTICE.START_DATE.le(currentLocalDateTime()))
                                .and(
                                        NOTICE.END_DATE.isNull()
                                                .or(NOTICE.END_DATE.ge(currentLocalDateTime()))
                                )
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Record를 NoticeEntity로 변환
     */
    private NoticeEntity mapToNoticeEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new NoticeEntity(
                record.get(NOTICE.NOTICE_ID),
                record.get(NOTICE.TITLE),
                record.get(NOTICE.CONTENT),
                record.get(NOTICE.CONTENT_TYPE),
                record.get(NOTICE.IS_PINNED),
                record.get(NOTICE.PIN_ORDER),
                record.get(NOTICE.START_DATE),
                record.get(NOTICE.END_DATE),
                record.get(NOTICE.VIEW_COUNT),
                record.get(NOTICE.CREATE_DATE),
                record.get(NOTICE.CREATE_USER_ID),
                record.get(NOTICE.UPDATED_DATE),
                record.get(NOTICE.UPDATE_USER_ID),
                record.get(NOTICE.DELETE_DATE),
                record.get(NOTICE.DELETE_USER_ID)
        );
    }
}
