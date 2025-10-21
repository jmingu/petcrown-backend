package kr.co.common.entity.notice;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class NoticeEntity {
    private Long noticeId;
    private String title;
    private String content;
    private String contentType;
    private String isPinned;
    private Integer pinOrder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;

    // BaseEntity 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updatedDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;
}