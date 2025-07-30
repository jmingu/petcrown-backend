package kr.co.api.domain.port.out;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.domain.model.vote.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 투표 조회 출력 포트 (Secondary Port)
 * 투표 데이터 읽기 관련 인터페이스
 */
public interface LoadVotePort {
    
    Optional<Vote> findById(Long voteId);
    
    Optional<Vote> findByPetIdAndVoteMonth(Long petId, LocalDate voteMonth);
    
    List<Vote> findByVoteMonth(LocalDate voteMonth);
    
    List<Vote> findByPetId(Long petId);
    
    List<VotePetResponseDto> getMonthlyTopVotes();
    
    boolean existsByPetIdAndVoteMonth(Long petId, LocalDate voteMonth);
    
    long countByVoteMonth(LocalDate voteMonth);
}