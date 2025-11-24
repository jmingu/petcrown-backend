package kr.co.api.notice.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공지사항 조회용 DTO
 */
@Getter
@AllArgsConstructor
public class NoticeQueryDto {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final String contentType;
    private final String isPinned;
    private final Integer pinOrder;
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
