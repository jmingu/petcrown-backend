package kr.co.api.community.domain;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CommunityComment {

    private final Long commentId;
    private final Long postId;
    private final Long userId;
    private final Long parentCommentId;
    private final String content;
    private final Long likeCount;
    private final Integer depth;
    private final Long createUserId;

    /**
     * ID로만 댓글 생성 (최소 정보)
     */
    public static CommunityComment ofId(Long commentId) {
        if (commentId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new CommunityComment(commentId, null, null, null, null, null, null, null);
    }

    /**
     * 새 댓글 생성 (최상위 댓글)
     */
    public static CommunityComment createComment(Long postId, Long userId, String content, Long createUserId) {
        if (postId == null || userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        if (content == null || content.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        return new CommunityComment(null, postId, userId, null, content.trim(), 0L, 0, createUserId);
    }

    /**
     * 새 대댓글 생성
     */
    public static CommunityComment createReply(Long postId, Long userId, Long parentCommentId,
                                               String content, Long createUserId) {
        if (postId == null || userId == null || parentCommentId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        if (content == null || content.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        return new CommunityComment(null, postId, userId, parentCommentId, content.trim(), 0L, 1, createUserId);
    }

    /**
     * 모든 필드로 댓글 생성
     */
    public static CommunityComment getAllFields(Long commentId, Long postId, Long userId,
                                               Long parentCommentId, String content, Long likeCount,
                                               Integer depth, Long createUserId) {
        return new CommunityComment(commentId, postId, userId, parentCommentId, content,
                                    likeCount, depth, createUserId);
    }

    /**
     * 좋아요 증가
     */
    public CommunityComment incrementLikeCount() {
        Long newLikeCount = this.likeCount != null ? this.likeCount + 1 : 1L;
        return new CommunityComment(this.commentId, this.postId, this.userId, this.parentCommentId,
                                    this.content, newLikeCount, this.depth, this.createUserId);
    }

    /**
     * 대댓글인지 확인
     */
    public boolean isReply() {
        return this.parentCommentId != null;
    }

    /**
     * 작성자 확인
     */
    public boolean isAuthor(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * 댓글 깊이 검증 (최대 1단계까지만 허용)
     */
    public void validateDepth() {
        if (this.depth != null && this.depth > 1) {
            throw new PetCrownException(BusinessCode.INVALID_COMMENT_DEPTH);
        }
    }
}
