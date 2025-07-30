package kr.co.api.domain.repository;

import kr.co.api.domain.model.vote.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoteRepository {
    
    Vote save(Vote vote);
    
    Optional<Vote> findById(Long voteId);
    
    Optional<Vote> findByPetIdAndVoteMonth(Long petId, LocalDate voteMonth);
    
    List<Vote> findByVoteMonth(LocalDate voteMonth);
    
    List<Vote> findByPetId(Long petId);
    
    boolean existsByPetIdAndVoteMonth(Long petId, LocalDate voteMonth);
    
    void delete(Vote vote);
    
    void deleteById(Long voteId);
    
    long countByVoteMonth(LocalDate voteMonth);
}