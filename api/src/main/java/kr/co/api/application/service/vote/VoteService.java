package kr.co.api.application.service.vote;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.api.application.port.in.vote.VoteUseCase;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.application.port.out.repository.vote.VoteRepositoryPort;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.vote.Vote;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * 투표 리스트 조회
     */
    @Override
    public Page<VotePetResponseDto> getVote(int page, int size) {
        // 현재 날짜에 해당하는 달의 1일
        LocalDate voteMonth = LocalDate.now().withDayOfMonth(1);
        log.debug("voteMonth: {}", voteMonth);

        Pageable pageable = PageRequest.of((page-1), size, Sort.by(Sort.Direction.DESC, "voteId"));

        // 투표 조회
        Page<VotePetResponseDto> vote = voteRepositoryPort.findVote(pageable);

        return vote;

    }

    /**
     * 투표 상세 조회
     */
    @Override
    public VotePetResponseDto getVoteDetail(Long voteId) {


        VotePetResponseDto voteDetailDto = voteRepositoryPort.findVoteDetail(voteId);
        if (voteDetailDto == null) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }
        // 현재 일자와 비교해서 아니면 에러 발생
        // 현재 날짜에 해당하는 달의 1일
        LocalDate voteMonth = LocalDate.now().withDayOfMonth(1);
        if (!voteDetailDto.getVoteMonth().equals(voteMonth)) {
            throw new PetCrownException(VOTE_NOT_FOUND);
        }

        return null;
    }


}
