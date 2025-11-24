package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 조회용 DTO (JOIN 결과)
 */
@Getter
@AllArgsConstructor
public class CommunityPostQueryDto {

    private final Long postId;
    private final Long userId;
    private final String nickname;
    private final String category;
    private final String title;
    private final String content;
    private final String contentType;
    private final Long viewCount;
    private final Long likeCount;
    private final Long commentCount;
    private final String isPinned;
    private final Integer pinOrder;
    private final LocalDateTime createDate;
    private final Long createUserId;
    private final LocalDateTime updateDate;
    private final Long updateUserId;
    private final LocalDateTime deleteDate;
    private final Long deleteUserId;
}
