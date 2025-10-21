package kr.co.api.notice.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeUpdateDto {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final String contentType;
    private final String isPinned;
    private final Integer pinOrder;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long updateUserId;
}