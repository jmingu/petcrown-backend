package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 댓글 조회용 DTO (JOIN 결과)
 */
@Getter
@AllArgsConstructor
public class CommunityCommentQueryDto {

    private final Long commentId;
    private final Long postId;
    private final Long userId;
    private final String nickname;
    private final Long parentCommentId;
    private final String content;
    private final Long likeCount;
    private final Integer depth;
    private final LocalDateTime createDate;
    private final Long createUserId;
    private final LocalDateTime updateDate;
    private final Long updateUserId;
    private final LocalDateTime deleteDate;
    private final Long deleteUserId;
}
