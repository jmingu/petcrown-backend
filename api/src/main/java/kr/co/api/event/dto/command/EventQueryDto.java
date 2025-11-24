package kr.co.api.event.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 이벤트 조회용 DTO
 */
@Getter
@AllArgsConstructor
public class EventQueryDto {

    private final Long eventId;
    private final String title;
    private final String content;
    private final String contentType;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long viewCount;
    private final LocalDateTime createDate;
    private final Long createUserId;
    private final LocalDateTime updatedDate;
    private final Long updateUserId;
    private final LocalDateTime deleteDate;
    private final Long deleteUserId;
}
