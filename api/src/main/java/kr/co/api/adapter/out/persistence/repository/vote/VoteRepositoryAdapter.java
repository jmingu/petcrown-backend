package kr.co.api.adapter.out.persistence.repository.vote;

import kr.co.api.domain.model.vote.Vote;
import kr.co.api.domain.repository.VoteRepository;
import kr.co.api.adapter.out.persistence.repository.vote.jpa.JpaVoteRepository;
import kr.co.api.converter.vote.VoteDomainEntityConverter;
import kr.co.common.entity.vote.VoteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryAdapter implements VoteRepository {
    
    private final JpaVoteRepository jpaVoteRepository;
    private final VoteDomainEntityConverter voteConverter;
    
    @Override
    public Vote save(Vote vote) {
        VoteEntity voteEntity = voteConverter.toEntity(vote);
        VoteEntity savedEntity = jpaVoteRepository.save(voteEntity);
        return voteConverter.toDomain(savedEntity);
    }
    
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
    public boolean existsByPetIdAndVoteMonth(Long petId, LocalDate voteMonth) {
        return jpaVoteRepository.existsByPet_PetIdAndVoteMonth(petId, voteMonth);
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
    
    @Override
    public long countByVoteMonth(LocalDate voteMonth) {
        return jpaVoteRepository.countByVoteMonth(voteMonth);
    }
}