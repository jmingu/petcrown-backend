package kr.co.api.event.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class EventInfoDto {

    private final Long eventId;
    private final String title;
    private final String content;
    private final String contentType;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long viewCount;
    private final LocalDateTime createDate;
    private final Long createUserId;
    private final String thumbnailUrl;
    private final List<String> imageUrls;
}
