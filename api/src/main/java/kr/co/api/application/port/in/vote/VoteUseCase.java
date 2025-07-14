package kr.co.api.application.port.in.vote;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import org.springframework.data.domain.Page;

public interface VoteUseCase {

    /**
     * 투표 등록
     */
    void createVote(Long petId, Long userId, String profileImageUrl);

    /**
     * 투표 조회
     */
    Page<VotePetResponseDto> getVote(int page, int size);

    /**
     * 투표 상세 조회
     */
    VotePetResponseDto getVoteDetail(Long voteId);
}
