package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커뮤니티 댓글 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class CommunityCommentDto {

    private final Long commentId;
    private final Long postId;
    private final Long userId;
    private final Long parentCommentId;
    private final String content;
    private final Long likeCount;
    private final Integer depth;
    private final Long createUserId;

    /**
     * 등록용 생성자 (commentId 없음)
     */
    public CommunityCommentDto(Long postId, Long userId, Long parentCommentId,
                              String content, Long likeCount, Integer depth, Long createUserId) {
        this(null, postId, userId, parentCommentId, content, likeCount, depth, createUserId);
    }
}
