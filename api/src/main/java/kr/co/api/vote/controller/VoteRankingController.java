package kr.co.api.vote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.command.VoteRankInfoDto;
import kr.co.api.vote.dto.response.VotePetRankResponseDto;
import kr.co.api.vote.dto.response.VoteRankingResponseDto;
import kr.co.api.vote.dto.response.VotePetResponseDto;
import kr.co.api.vote.service.VoteRankingService;
import kr.co.common.contoller.BaseController;
import kr.co.common.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/ranking")
@Tag(name = "Ranking", description = "랭킹 관련 API")
@Validated
public class VoteRankingController extends BaseController {

    private final VoteRankingService voteRankingService;

    /**
     * 이번 주 Top 3 랭킹 조회 (실시간)
     */
    @AuthRequired(authSkip = true)
    @GetMapping("/v1/current-week")
    @Operation(summary = "이번 주 Top 3 랭킹 조회", description = "현재 주차(월~금) 실시간 투표 랭킹 1~3위 조회")
    public ResponseEntity<CommonResponseDto> getCurrentWeekTopRanking() {

        List<VoteRankInfoDto> ranking = voteRankingService.getCurrentWeekTopRanking();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<VotePetRankResponseDto> rankingList = ranking.stream()
                .map(dto -> new VotePetRankResponseDto(
                        dto.getVoteId(),
                        dto.getPetId(),
                        dto.getRank(),
                        dto.getNickname(),
                        dto.getName(),
                        dto.getGender(),
                        dto.getBirthDate(),
                        dto.getBreedId(),
                        dto.getBreedName(),
                        dto.getCustomBreed(),
                        dto.getSpeciesId(),
                        dto.getSpeciesName(),
                        dto.getPetModeId(),
                        dto.getPetModeName(),
                        dto.getDailyVoteCount(),
                        dto.getWeeklyVoteCount(),
                        dto.getMonthlyVoteCount(),
                        dto.getVoteMonth(),
                        dto.getProfileImageUrl(),
                        dto.getOwnerEmail()
                ))
                .collect(Collectors.toList());

        VoteRankingResponseDto response = new VoteRankingResponseDto(rankingList);

        return success(response);
    }

    /**
     * 지난 주 Top 3 랭킹 조회
     */
    @AuthRequired(authSkip = true)
    @GetMapping("/v1/last-week")
    @Operation(summary = "지난 주 Top 3 랭킹 조회", description = "지난 주차(월~금) 투표 랭킹 1~3위 조회")
    public ResponseEntity<CommonResponseDto> getLastWeekTopRanking() {

        List<VoteRankInfoDto> ranking = voteRankingService.getLastWeekTopRanking();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<VotePetRankResponseDto> rankingList = ranking.stream()
                .map(dto -> new VotePetRankResponseDto(
                        dto.getVoteId(),
                        dto.getPetId(),
                        dto.getRank(),
                        dto.getNickname(),
                        dto.getName(),
                        dto.getGender(),
                        dto.getBirthDate(),
                        dto.getBreedId(),
                        dto.getBreedName(),
                        dto.getCustomBreed(),
                        dto.getSpeciesId(),
                        dto.getSpeciesName(),
                        dto.getPetModeId(),
                        dto.getPetModeName(),
                        dto.getDailyVoteCount(),
                        dto.getWeeklyVoteCount(),
                        dto.getMonthlyVoteCount(),
                        dto.getVoteMonth(),
                        dto.getProfileImageUrl(),
                        dto.getOwnerEmail()
                ))
                .collect(Collectors.toList());

        VoteRankingResponseDto response = new VoteRankingResponseDto(rankingList);

        return success(response);
    }

    /**
     * 이번 주 내 랭킹 조회
     */
    @GetMapping("/v1/my-current-week")
    @Operation(summary = "이번 주 내 랭킹 조회", description = "현재 주차(월~금) 실시간 투표 내 랭킹 조회")
    public ResponseEntity<CommonResponseDto> getCurrentWeekMyRanking(Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        VoteRankInfoDto dto = voteRankingService.getCurrentWeekMyRanking(userId);

        if (dto == null) {
            return success(null);
        }

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)

        return success(new VotePetRankResponseDto(
                dto.getVoteId(),
                dto.getPetId(),
                dto.getRank(),
                dto.getNickname(),
                dto.getName(),
                dto.getGender(),
                dto.getBirthDate(),
                dto.getBreedId(),
                dto.getBreedName(),
                dto.getCustomBreed(),
                dto.getSpeciesId(),
                dto.getSpeciesName(),
                dto.getPetModeId(),
                dto.getPetModeName(),
                dto.getDailyVoteCount(),
                dto.getWeeklyVoteCount(),
                dto.getMonthlyVoteCount(),
                dto.getVoteMonth(),
                dto.getProfileImageUrl(),
                dto.getOwnerEmail()
        ));
    }
}
