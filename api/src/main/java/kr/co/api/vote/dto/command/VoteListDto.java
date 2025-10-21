package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VoteListDto {

    private final List<VoteInfoDto> votes;
    private final int totalCount;
}