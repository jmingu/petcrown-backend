package kr.co.api.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentUpdateRequestDto {

    @Schema(description = "댓글 ID", required = true)
    private Long commentId;

    @Schema(description = "댓글 내용", required = true)
    private String content;
}
