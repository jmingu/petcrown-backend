package kr.co.api.adapter.out.persistence.repository.vote;

import kr.co.api.adapter.out.persistence.repository.vote.jpa.JpaVoteRepository;
import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.application.port.out.repository.vote.VoteRepositoryPort;
import kr.co.api.converter.vote.VoteDomainEntityConverter;
import kr.co.api.domain.model.vote.Vote;
import kr.co.common.entity.vote.VoteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VoteRepository implements VoteRepositoryPort {

    private final JpaVoteRepository jpaVoteRepository;
    private final VoteDomainEntityConverter voteDomainEntityConverter;

    /**
     * 이번달 투표 조회
     */
    @Override
    public Vote findVoteByPetIdAndVoteMonth(Long petId, LocalDate voteMonth) {
        Optional<VoteEntity> entity = jpaVoteRepository.findVoteByPet_PetIdAndVoteMonthAndDeleteYn(petId, voteMonth, "N");
        if( entity.isPresent()) {
            log.debug("Vote found for petId: {}, voteMonth: {}", petId, voteMonth);
            return voteDomainEntityConverter.toDomainWithNullPet(entity.get());
        }

        return null;
    }

    /**
     * 투표 등록
     */
    @Override
    public void saveVote(Vote vote) {
        log.debug("Saving vote: {}", vote);

        VoteEntity entity = voteDomainEntityConverter.toEntity(vote);
        jpaVoteRepository.save(entity);
        log.debug("Vote saved successfully");
    }

    /**
     * 투표 리스트 조회
     */
    @Override
    public Page<VotePetResponseDto> findVote(Pageable pageable) {

        Page<VotePetResponseDto> dtoPage = jpaVoteRepository.findAllByDeleteYn("N", pageable);


        return dtoPage;
    }

    /**
     * 투표 상세 조회
     */
    @Override
    public VotePetResponseDto findVoteDetail(Long voteId) {
        VotePetResponseDto dto = jpaVoteRepository.findVoteDetail(voteId);
        return dto;
    }


}
