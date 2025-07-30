package kr.co.api.application.service;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.vote.Vote;
import kr.co.api.domain.port.in.ManageVotePort;
import kr.co.api.domain.port.out.LoadPetPort;
import kr.co.api.domain.port.out.LoadUserPort;
import kr.co.api.domain.port.out.LoadVotePort;
import kr.co.api.domain.port.out.SaveVotePort;
import kr.co.api.domain.service.VoteDomainService;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 투표 관리 애플리케이션 서비스
 * ManageVotePort 구현체 (입력 포트 구현)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoteManagementService implements ManageVotePort {
    
    private final LoadVotePort loadVotePort;
    private final SaveVotePort saveVotePort;
    private final LoadPetPort loadPetPort;
    private final LoadUserPort loadUserPort;
    private final VoteDomainService voteDomainService;
    
    @Override
    @Transactional
    public void registerVote(Vote vote) {
        Pet pet = loadPetPort.findById(vote.getPetId())
                .orElseThrow(() -> new PetCrownException(BusinessCode.PET_NOT_FOUND));
        
        User user = loadUserPort.findById(pet.getUserId())
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        // 도메인 비즈니스 로직 검증
        voteDomainService.validateVoteRegistration(vote, pet, user);
        
        // 중복 투표 확인
        if (loadVotePort.existsByPetIdAndVoteMonth(vote.getPetId(), vote.getVoteMonth())) {
            throw new PetCrownException(BusinessCode.VOTE_ALREADY_REGISTERED);
        }
        
        Vote savedVote = saveVotePort.save(vote);
        
        log.info("Vote registered successfully: voteId={}, petId={}", savedVote.getVoteId(), vote.getPetId());
    }
    
    @Override
    public List<VotePetResponseDto> getMonthlyVotes() {
        return loadVotePort.getMonthlyTopVotes();
    }
    
    @Override
    @Transactional
    public void deleteVote(Long voteId, Long userId) {
        Vote vote = loadVotePort.findById(voteId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.VOTE_NOT_FOUND));
        
        Pet pet = loadPetPort.findById(vote.getPetId())
                .orElseThrow(() -> new PetCrownException(BusinessCode.PET_NOT_FOUND));
        
        // 투표 소유자 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(BusinessCode.PET_NOT_OWNED);
        }
        
        // 도메인 비즈니스 로직: 투표 삭제 가능 여부 확인
        if (!voteDomainService.canDeletePetWithVote(pet, vote.getVoteMonth())) {
            throw new PetCrownException(BusinessCode.VOTE_CANNOT_DELETE);
        }
        
        saveVotePort.deleteById(voteId);
        
        log.info("Vote deleted successfully: voteId={}, userId={}", voteId, userId);
    }
}