package kr.co.api.vote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.vote.converter.dtoCommand.VoteDtoCommandConverter;
import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.response.VoteRankingResponseDto;
import kr.co.api.vote.service.VoteRankingService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/ranking")
@Tag(name = "Ranking", description = "랭킹 관련 API")
@Validated
public class VoteRankingController extends BaseController {

    private final VoteRankingService voteRankingService;
    private final VoteDtoCommandConverter voteDtoCommandConverter;

    /**
     * 이번 주 Top 3 랭킹 조회 (실시간)
     */
    @AuthRequired(authSkip = true)
    @GetMapping("/v1/current-week")
    @Operation(summary = "이번 주 Top 3 랭킹 조회", description = "현재 주차(월~금) 실시간 투표 랭킹 1~3위 조회")
    public ResponseEntity<CommonResponseDto> getCurrentWeekTopRanking() {

        List<VoteInfoDto> ranking = voteRankingService.getCurrentWeekTopRanking();
        VoteRankingResponseDto response = voteDtoCommandConverter.toRankingResponseDto(ranking);

        return success(response);
    }

    /**
     * 지난 주 Top 3 랭킹 조회
     */
    @AuthRequired(authSkip = true)
    @GetMapping("/v1/last-week")
    @Operation(summary = "지난 주 Top 3 랭킹 조회", description = "지난 주차(월~금) 투표 랭킹 1~3위 조회")
    public ResponseEntity<CommonResponseDto> getLastWeekTopRanking() {

        List<VoteInfoDto> ranking = voteRankingService.getLastWeekTopRanking();
        VoteRankingResponseDto response = voteDtoCommandConverter.toRankingResponseDto(ranking);

        return success(response);
    }
}
