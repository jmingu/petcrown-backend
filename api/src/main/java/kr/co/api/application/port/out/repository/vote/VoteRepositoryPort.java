package kr.co.api.application.port.out.repository.vote;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.domain.model.vote.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface VoteRepositoryPort {


    /**
     * 투표가 이미 등록되었는지 확인
     */
    Vote findVoteByPetIdAndVoteMonth(Long petId, LocalDate voteMonth);

    /**
     * 투표 등록
     */
    void saveVote(Vote vote);

    /**
     * 투표 조회
     */
    Page<VotePetResponseDto> findVote(Pageable pageable);

    /**
     * 투표 상세 조회
     */
    VotePetResponseDto findVoteDetail(Long voteId);




}
