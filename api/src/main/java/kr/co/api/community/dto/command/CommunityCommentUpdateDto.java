package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityCommentUpdateDto {

    private final Long commentId;
    private final String content;
    private final Long updateUserId;
}
