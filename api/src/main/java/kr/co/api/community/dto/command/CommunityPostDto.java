package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커뮤니티 게시글 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class CommunityPostDto {

    private final Long postId;
    private final Long userId;
    private final String category;
    private final String title;
    private final String content;
    private final String contentType;
    private final Long viewCount;
    private final Long likeCount;
    private final Long commentCount;
    private final String isPinned;
    private final Integer pinOrder;
    private final Long createUserId;

    /**
     * 등록용 생성자 (postId 없음)
     */
    public CommunityPostDto(Long userId, String category, String title, String content,
                           String contentType, Long viewCount, Long likeCount,
                           Long commentCount, String isPinned, Integer pinOrder, Long createUserId) {
        this(null, userId, category, title, content, contentType, viewCount, likeCount,
            commentCount, isPinned, pinOrder, createUserId);
    }
}
