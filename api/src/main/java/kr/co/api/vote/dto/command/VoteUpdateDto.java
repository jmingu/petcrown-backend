package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteUpdateDto {

    private final Long voteId;
    private final Long userId;
    private final Long petId;
    private final String profileImageUrl;
    private final Integer petModeId;
}