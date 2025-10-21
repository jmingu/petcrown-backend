package kr.co.api.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostResponseDto {

    private Long postId;
    private String nickname;
    private String category;
    private String title;
    private String content;
    private String contentType;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String isPinned;
    private Integer pinOrder;
    private LocalDateTime createDate;
    private List<String> imageUrls;
}
