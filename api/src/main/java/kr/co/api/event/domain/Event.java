package kr.co.api.event.domain;

import kr.co.api.notice.domain.vo.Content;
import kr.co.api.notice.domain.vo.ContentType;
import kr.co.api.notice.domain.vo.Title;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Event {

    private final Long eventId;
    private final Title title;
    private final Content content;
    private final ContentType contentType;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long viewCount;
    private final Long createUserId;

    /**
     * ID로만 이벤트 생성 (최소 정보)
     */
    public static Event ofId(Long eventId) {
        if (eventId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new Event(eventId, null, null, null, null, null, null, null);
    }

    /**
     * 새 이벤트 생성
     */
    public static Event createEvent(String titleValue, String contentValue, String contentTypeValue,
                                    LocalDateTime startDate, LocalDateTime endDate, Long createUserId) {

        // Value Objects 생성 (유효성 검증 포함)
        Title title = Title.of(titleValue);
        Content content = Content.of(contentValue);
        ContentType contentType = ContentType.of(contentTypeValue);

        // 기본값 설정
        Long viewCount = 0L;

        // 시작일이 없으면 현재 시간으로 설정
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now();

        return new Event(null, title, content, contentType, start, endDate, viewCount, createUserId);
    }

    /**
     * 모든 필드로 이벤트 생성
     */
    public static Event getAllFields(Long eventId, Title title, Content content, ContentType contentType,
                                     LocalDateTime startDate, LocalDateTime endDate, Long viewCount, Long createUserId) {
        return new Event(eventId, title, content, contentType, startDate, endDate, viewCount, createUserId);
    }

    /**
     * 조회수 증가
     */
    public Event incrementViewCount() {
        Long newViewCount = this.viewCount != null ? this.viewCount + 1 : 1L;
        return new Event(this.eventId, this.title, this.content, this.contentType,
                        this.startDate, this.endDate, newViewCount, this.createUserId);
    }

    /**
     * 이벤트가 현재 활성화되어 있는지 확인
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();

        // 시작일이 지났는지 확인
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }

        // 종료일이 있고 지났는지 확인
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }

        return true;
    }

    /**
     * ID가 설정된 새 객체 반환 (불변성 유지)
     */
    public Event withId(Long eventId) {
        return new Event(
            eventId,
            this.title,
            this.content,
            this.contentType,
            this.startDate,
            this.endDate,
            this.viewCount,
            this.createUserId
        );
    }
}
