package kr.co.api.application.port.out.repository.vote;

import kr.co.api.domain.model.vote.Vote;

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




}
