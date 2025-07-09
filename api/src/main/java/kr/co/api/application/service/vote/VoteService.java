package kr.co.api.application.service.vote;

import kr.co.api.application.port.in.vote.VoteUseCase;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.application.port.out.repository.vote.VoteRepositoryPort;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.vote.Vote;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static kr.co.common.enums.BusinessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoteService implements VoteUseCase {

    private final PetRepositoryPort petRepositoryPort;
     private final VoteRepositoryPort voteRepositoryPort;

    /**
     * 투표 등록
     */
    @Transactional
    @Override
    public void createVote(Long petId, Long userId, String profileImageUrl) {

        // 현재 날짜에 해당하는 달의 1일
        LocalDate voteMonth = LocalDate.now().withDayOfMonth(1);
        log.debug("voteMonth: {}", voteMonth);

        // 이번달 투표가 이미 등록되었는지 확인
        Vote voteByPetIdAndVoteMonth = voteRepositoryPort.findVoteByPetIdAndVoteMonth(petId, voteMonth);
        if (voteByPetIdAndVoteMonth != null) {
            // 이미 등록된 투표가 있다면 예외 처리
            throw new PetCrownException(VOTE_ALREADY_REGISTERED);
        }


        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 내 소유 펫인지 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }


        // 투표 객체 생성
         Vote vote = Vote.createVote(pet, voteMonth, profileImageUrl);
        log.debug("vote: {}", vote);
        // 투표 등록
        voteRepositoryPort.saveVote(vote);


    }
}
