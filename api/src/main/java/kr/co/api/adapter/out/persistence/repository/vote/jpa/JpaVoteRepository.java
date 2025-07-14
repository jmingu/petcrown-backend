package kr.co.api.adapter.out.persistence.repository.vote.jpa;

import kr.co.api.application.dto.vote.response.VotePetResponseDto;
import kr.co.common.entity.vote.VoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface JpaVoteRepository extends JpaRepository<VoteEntity, Long> {

    /**
     * 투표가 이미 등록되었는지 확인
     */
     Optional<VoteEntity>findVoteByPet_PetIdAndVoteMonthAndDeleteYn(Long petId, LocalDate voteMonth, String deleteYn);

    /**
     * 투표 조회
     */
    @Query(value = "SELECT new kr.co.api.application.dto.vote.response.VotePetResponseDto(v.voteId, v.pet.petId, v.pet.name, v.pet.gender, v.pet.birthDate, v.pet.breed.breedId, v.pet.breed.name, v.pet.breed.species.speciesId, v.pet.breed.species.name, v.dailyVoteCount, v.weeklyVoteCount, v.monthlyVoteCount, v.voteMonth, v.profileImageUrl) " +
            "FROM VoteEntity v " +
            "WHERE v.deleteYn = :deleteYn",
            countQuery = "SELECT COUNT(v) FROM VoteEntity v WHERE v.deleteYn = :deleteYn")
    Page<VotePetResponseDto> findAllByDeleteYn(String deleteYn, Pageable pageable);

    /**
     * 투표 상세 조회
     */
    @Query(value = "SELECT new kr.co.api.application.dto.vote.response.VotePetResponseDto(v.voteId, v.pet.petId, v.pet.name, v.pet.gender, v.pet.birthDate, v.pet.breed.breedId, v.pet.breed.name, v.pet.breed.species.speciesId, v.pet.breed.species.name, v.dailyVoteCount, v.weeklyVoteCount, v.monthlyVoteCount, v.voteMonth, v.profileImageUrl) " +
            "FROM VoteEntity v " +
            "WHERE v.deleteYn = :deleteYn " +
            "AND v.voteId = :voteId")
    VotePetResponseDto findVoteDetail(Long voteId);



}
