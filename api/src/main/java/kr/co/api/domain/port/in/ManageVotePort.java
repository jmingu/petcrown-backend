package kr.co.api.domain.port.in;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.domain.model.vote.Vote;

import java.util.List;

/**
 * 투표 관리 입력 포트 (Primary Port)
 * 투표 관련 Use Case를 정의
 */
public interface ManageVotePort {
    
    void registerVote(Vote vote);
    
    List<VotePetResponseDto> getMonthlyVotes();
    
    void deleteVote(Long voteId, Long userId);
}