package kr.co.common.entity.community;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 조회 전용 DTO
 * JOIN 결과를 담기 위한 DTO (user 테이블 JOIN)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CommunityPostQueryDto {
    private Long postId;
    private Long userId;
    private String nickname;  // user 테이블 JOIN 결과
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
