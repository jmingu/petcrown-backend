package kr.co.api.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponseDto {

    private Long eventId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;
    private LocalDateTime createDate;
    private String thumbnailUrl;
}
