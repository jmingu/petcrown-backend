package kr.co.api.adapter.out.persistence;

import kr.co.api.adapter.out.persistence.repository.vote.jpa.JpaVoteRepository;
import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.converter.vote.VoteDomainEntityConverter;
import kr.co.api.domain.model.vote.Vote;
import kr.co.api.domain.port.out.LoadVotePort;
import kr.co.api.domain.port.out.SaveVotePort;
import kr.co.common.entity.vote.VoteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 투표 영속성 어댑터 (Output Adapter)
 * LoadVotePort, SaveVotePort 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VotePersistenceAdapter implements LoadVotePort, SaveVotePort {
    
    private final JpaVoteRepository jpaVoteRepository;
    private final VoteDomainEntityConverter voteConverter;
    
    // LoadVotePort 구현
    @Override
    public Optional<Vote> findById(Long voteId) {
        return jpaVoteRepository.findById(voteId)
                .map(voteConverter::toDomain);
    }
    
    @Override
    public Optional<Vote> findByPetIdAndVoteMonth(Long petId, LocalDate voteMonth) {
        return jpaVoteRepository.findByPet_PetIdAndVoteMonth(petId, voteMonth)
                .map(voteConverter::toDomain);
    }
    
    @Override
    public List<Vote> findByVoteMonth(LocalDate voteMonth) {
        return jpaVoteRepository.findByVoteMonth(voteMonth)
                .stream()
                .map(voteConverter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vote> findByPetId(Long petId) {
        return jpaVoteRepository.findByPet_PetId(petId)
                .stream()
                .map(voteConverter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VotePetResponseDto> getMonthlyTopVotes() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        
        return jpaVoteRepository.findByVoteMonth(currentMonth)
                .stream()
                .map(voteEntity -> new VotePetResponseDto(
                        voteEntity.getVoteId(),
                        voteEntity.getPet().getPetId(),
                        voteEntity.getPet().getName(),
                        voteEntity.getPet().getProfileImageUrl(),
                        voteEntity.getVoteCount(),
                        voteEntity.getVoteMonth()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByPetIdAndVoteMonth(Long petId, LocalDate voteMonth) {
        return jpaVoteRepository.existsByPet_PetIdAndVoteMonth(petId, voteMonth);
    }
    
    @Override
    public long countByVoteMonth(LocalDate voteMonth) {
        return jpaVoteRepository.countByVoteMonth(voteMonth);
    }
    
    // SaveVotePort 구현
    @Override
    public Vote save(Vote vote) {
        VoteEntity voteEntity = voteConverter.toEntity(vote);
        VoteEntity savedEntity = jpaVoteRepository.save(voteEntity);
        return voteConverter.toDomain(savedEntity);
    }
    
    @Override
    public void delete(Vote vote) {
        VoteEntity voteEntity = voteConverter.toEntity(vote);
        jpaVoteRepository.delete(voteEntity);
    }
    
    @Override
    public void deleteById(Long voteId) {
        jpaVoteRepository.deleteById(voteId);
    }
}