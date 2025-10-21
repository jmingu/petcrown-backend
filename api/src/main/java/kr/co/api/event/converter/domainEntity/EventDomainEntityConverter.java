package kr.co.api.event.converter.domainEntity;

import kr.co.api.event.domain.Event;
import kr.co.api.notice.domain.vo.Content;
import kr.co.api.notice.domain.vo.ContentType;
import kr.co.api.notice.domain.vo.Title;
import kr.co.common.entity.event.EventEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Event 도메인과 Entity 간 변환을 담당하는 Converter
 */
@Component
public class EventDomainEntityConverter {

    /**
     * 이벤트 생성용 Event 도메인 → EventEntity 변환
     */
    public EventEntity toEventEntityForRegistration(Event event) {
        if (event == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();

        return new EventEntity(
                event.getEventId(),
                event.getTitle().getValue(),
                event.getContent().getValue(),
                event.getContentType().getValue(),
                event.getStartDate(),
                event.getEndDate(),
                event.getViewCount(),
                now,
                event.getCreateUserId(),
                now,
                event.getCreateUserId(),
                null,
                null
        );
    }

    /**
     * 이벤트 수정용 Event 도메인 → EventEntity 변환
     */
    public EventEntity toEventEntityForUpdate(Event event, Long updateUserId) {
        if (event == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();

        return new EventEntity(
                event.getEventId(),
                event.getTitle().getValue(),
                event.getContent().getValue(),
                event.getContentType().getValue(),
                event.getStartDate(),
                event.getEndDate(),
                event.getViewCount(),
                null, // createDate는 업데이트 시 변경하지 않음
                null, // createUserId는 업데이트 시 변경하지 않음
                now,
                updateUserId,
                null,
                null
        );
    }

    /**
     * EventEntity → Event 도메인 변환
     */
    public Event toEventDomain(EventEntity entity) {
        if (entity == null) {
            return null;
        }

        return Event.getAllFields(
                entity.getEventId(),
                Title.of(entity.getTitle()),
                Content.of(entity.getContent()),
                ContentType.of(entity.getContentType()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getViewCount(),
                entity.getCreateUserId()
        );
    }
}
