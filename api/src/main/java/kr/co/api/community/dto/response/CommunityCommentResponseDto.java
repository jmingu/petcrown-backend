package kr.co.api.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentResponseDto {

    private Long commentId;
    private Long postId;
    private String nickname;
    private Long parentCommentId;
    private String content;
    private Long likeCount;
    private Integer depth;
    private LocalDateTime createDate;
    private List<CommunityCommentResponseDto> replies;  // 대댓글 리스트
}
