package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityCommentRegistrationDto {

    private final Long postId;
    private final Long userId;
    private final Long parentCommentId;  // null이면 댓글, 값이 있으면 대댓글
    private final String content;
    private final Long createUserId;
}
