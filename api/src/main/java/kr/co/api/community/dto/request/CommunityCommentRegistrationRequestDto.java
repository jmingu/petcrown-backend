package kr.co.api.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentRegistrationRequestDto {

    @Schema(description = "게시글 ID", required = true)
    private Long postId;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", required = false)
    private Long parentCommentId;

    @Schema(description = "댓글 내용", required = true)
    private String content;
}
