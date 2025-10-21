package kr.co.api.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {

    private Long noticeId;
    private String title;
    private String content;
    private String contentType;
    private String isPinned;
    private Integer pinOrder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;
    private LocalDateTime createDate;
    private Long createUserId;
}