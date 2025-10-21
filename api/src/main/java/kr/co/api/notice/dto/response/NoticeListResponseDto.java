package kr.co.api.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListResponseDto {

    private Long noticeId;
    private String title;
    private String isPinned;
    private Integer pinOrder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;
    private LocalDateTime createDate;
}