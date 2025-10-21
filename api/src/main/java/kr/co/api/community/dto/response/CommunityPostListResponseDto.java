package kr.co.api.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostListResponseDto {

    private Long postId;
    private String nickname;
    private String category;
    private String title;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String isPinned;
    private Integer pinOrder;
    private LocalDateTime createDate;
}
