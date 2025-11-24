package kr.co.api.event.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 이벤트 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class EventDto {

    private final Long eventId;
    private final String title;
    private final String content;
    private final String contentType;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long viewCount;
    private final Long createUserId;

    /**
     * 등록용 생성자 (eventId 없음)
     */
    public EventDto(String title, String content, String contentType,
                   LocalDateTime startDate, LocalDateTime endDate, Long viewCount, Long createUserId) {
        this(null, title, content, contentType, startDate, endDate, viewCount, createUserId);
    }
}
