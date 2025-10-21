package kr.co.api.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

    private Long eventId;
    private String title;
    private String content;
    private String contentType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;
    private LocalDateTime createDate;
    private Long createUserId;
    private String thumbnailUrl;
    private List<String> imageUrls;
}
