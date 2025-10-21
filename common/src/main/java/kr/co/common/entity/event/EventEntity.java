package kr.co.common.entity.event;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class EventEntity {
    private Long eventId;
    private String title;
    private String content;
    private String contentType;
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