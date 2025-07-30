package kr.co.api.domain.port.out;

import kr.co.api.domain.model.vote.Vote;

/**
 * 투표 저장 출력 포트 (Secondary Port)
 * 투표 데이터 쓰기 관련 인터페이스
 */
public interface SaveVotePort {
    
    Vote save(Vote vote);
    
    void delete(Vote vote);
    
    void deleteById(Long voteId);
}