package kr.co.api.community.domain;

import kr.co.api.notice.domain.vo.Content;
import kr.co.api.notice.domain.vo.ContentType;
import kr.co.api.notice.domain.vo.Title;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CommunityPost {

    private final Long postId;
    private final Long userId;
    private final String category;
    private final Title title;
    private final Content content;
    private final ContentType contentType;
    private final Long viewCount;
    private final Long likeCount;
    private final Long commentCount;
    private final String isPinned;
    private final Integer pinOrder;
    private final Long createUserId;

    /**
     * ID로만 게시글 생성 (최소 정보)
     */
    public static CommunityPost ofId(Long postId) {
        if (postId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new CommunityPost(postId, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * 새 게시글 생성
     */
    public static CommunityPost createPost(Long userId, String category, String titleValue, String contentValue,
                                          String contentTypeValue, Long createUserId) {

        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // Value Objects 생성 (유효성 검증 포함)
        Title title = Title.of(titleValue);
        Content content = Content.of(contentValue);
        ContentType contentType = ContentType.of(contentTypeValue);

        // 기본값 설정
        Long viewCount = 0L;
        Long likeCount = 0L;
        Long commentCount = 0L;
        String isPinned = "N";
        Integer pinOrder = 1;

        return new CommunityPost(null, userId, category, title, content, contentType, viewCount,
                                likeCount, commentCount, isPinned, pinOrder, createUserId);
    }

    /**
     * 모든 필드로 게시글 생성
     */
    public static CommunityPost getAllFields(Long postId, Long userId, String category, Title title, Content content,
                                            ContentType contentType, Long viewCount, Long likeCount,
                                            Long commentCount, String isPinned, Integer pinOrder,
                                            Long createUserId) {
        return new CommunityPost(postId, userId, category, title, content, contentType, viewCount,
                                likeCount, commentCount, isPinned, pinOrder, createUserId);
    }

    /**
     * 조회수 증가
     */
    public CommunityPost incrementViewCount() {
        Long newViewCount = this.viewCount != null ? this.viewCount + 1 : 1L;
        return new CommunityPost(this.postId, this.userId, this.category, this.title, this.content,
                                this.contentType, newViewCount, this.likeCount, this.commentCount,
                                this.isPinned, this.pinOrder, this.createUserId);
    }

    /**
     * 좋아요 증가
     */
    public CommunityPost incrementLikeCount() {
        Long newLikeCount = this.likeCount != null ? this.likeCount + 1 : 1L;
        return new CommunityPost(this.postId, this.userId, this.category, this.title, this.content,
                                this.contentType, this.viewCount, newLikeCount, this.commentCount,
                                this.isPinned, this.pinOrder, this.createUserId);
    }

    /**
     * 댓글 수 증가
     */
    public CommunityPost incrementCommentCount() {
        Long newCommentCount = this.commentCount != null ? this.commentCount + 1 : 1L;
        return new CommunityPost(this.postId, this.userId, this.category, this.title, this.content,
                                this.contentType, this.viewCount, this.likeCount, newCommentCount,
                                this.isPinned, this.pinOrder, this.createUserId);
    }

    /**
     * 댓글 수 감소
     */
    public CommunityPost decrementCommentCount() {
        Long newCommentCount = this.commentCount != null && this.commentCount > 0 ? this.commentCount - 1 : 0L;
        return new CommunityPost(this.postId, this.userId, this.category, this.title, this.content,
                                this.contentType, this.viewCount, this.likeCount, newCommentCount,
                                this.isPinned, this.pinOrder, this.createUserId);
    }

    /**
     * 작성자 확인
     */
    public boolean isAuthor(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * 상단 고정 게시글인지 확인
     */
    public boolean isPinned() {
        return "Y".equals(this.isPinned);
    }
}
