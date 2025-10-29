package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityCommentInfoDto {

    private final Long commentId;
    private final Long postId;
    private final String nickname;
    private final Long parentCommentId;
    private final String content;
    private final Long likeCount;
    private final Integer depth;
    private final LocalDateTime createDate;
    private final String commentWriteYn;  // 작성자 여부 (Y/N)
    private final List<CommunityCommentInfoDto> replies;  // 대댓글 리스트
}
