package kr.co.common.entity.community;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CommunityPostEntity {
    private Long postId;
    private Long userId;
    private String category;
    private String title;
    private String content;
    private String contentType;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String isPinned;
    private Integer pinOrder;

    // BaseEntity 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;
}
