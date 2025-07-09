package kr.co.api.adapter.out.persistence.repository.vote.jpa;

import kr.co.common.entity.vote.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JpaVoteRepository extends JpaRepository<VoteEntity, Long> {

    /**
     * 투표가 이미 등록되었는지 확인
     */
     Optional<VoteEntity>findVoteByPet_PetIdAndVoteMonthAndDeleteYn(Long petId, LocalDate voteMonth, String deleteYn);


}
