package kr.co.api.notice.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공지사항 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class NoticeDto {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final String contentType;
    private final String isPinned;
    private final Integer pinOrder;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long viewCount;
    private final Long createUserId;

    /**
     * 등록용 생성자 (noticeId 없음)
     */
    public NoticeDto(String title, String content, String contentType, String isPinned,
                    Integer pinOrder, LocalDateTime startDate, LocalDateTime endDate,
                    Long viewCount, Long createUserId) {
        this(null, title, content, contentType, isPinned, pinOrder, startDate, endDate, viewCount, createUserId);
    }
}
