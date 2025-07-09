package kr.co.api.application.port.in.vote;

public interface VoteUseCase {

    /**
     * 투표 등록
     */
    void createVote(Long petId, Long userId, String profileImageUrl);
}
