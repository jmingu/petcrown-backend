package kr.co.api.event.converter.dtoDomain;

import kr.co.api.event.domain.Event;
import kr.co.api.event.dto.command.EventRegistrationDto;
import kr.co.api.event.dto.command.EventUpdateDto;
import org.springframework.stereotype.Component;

/**
 * Event Command DTO와 Domain 객체 간 변환을 담당하는 Converter
 */
@Component
public class EventDtoDomainConverter {

    /**
     * 이벤트 생성용 Event 도메인 객체 생성
     *
     * @param dto 이벤트 등록 CommandDto
     * @return Event 도메인 객체
     */
    public Event toEventForRegistration(EventRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        return Event.createEvent(
                dto.getTitle(),
                dto.getContent(),
                dto.getContentType(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getCreateUserId()
        );
    }

    /**
     * 이벤트 수정용 Event 도메인 객체 생성
     *
     * @param dto 이벤트 수정 CommandDto
     * @return Event 도메인 객체
     */
    public Event toEventForUpdate(EventUpdateDto dto) {
        if (dto == null) {
            return null;
        }

        return Event.createEvent(
                dto.getTitle(),
                dto.getContent(),
                dto.getContentType(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getUpdateUserId()
        );
    }
}
