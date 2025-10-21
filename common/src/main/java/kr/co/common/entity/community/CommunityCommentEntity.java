package kr.co.common.entity.community;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CommunityCommentEntity {
    private Long commentId;
    private Long postId;
    private Long userId;
    private Long parentCommentId;  // 대댓글인 경우 부모 댓글 ID
    private String content;
    private Long likeCount;
    private Integer depth;  // 댓글 깊이 (0: 댓글, 1: 대댓글)

    // BaseEntity 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;
}
