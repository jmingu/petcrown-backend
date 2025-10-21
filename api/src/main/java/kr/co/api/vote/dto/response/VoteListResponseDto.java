package kr.co.api.vote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteListResponseDto {
    
    private List<VotePetResponseDto> votes;
    private int totalCount;
}