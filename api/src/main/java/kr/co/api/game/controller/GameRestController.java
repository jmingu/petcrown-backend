package kr.co.api.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.game.dto.command.GameScoreDto;
import kr.co.api.game.dto.command.GameScoreRegistrationDto;
import kr.co.api.game.dto.command.WeeklyRankingDto;
import kr.co.api.game.dto.request.GameScoreRegistrationRequestDto;
import kr.co.api.game.dto.response.MyWeeklyScoreResponseDto;
import kr.co.api.game.dto.response.WeeklyRankingListResponseDto;
import kr.co.api.game.service.GameScoreService;
import kr.co.common.contoller.BaseController;
import kr.co.common.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Game", description = "게임 관련 API")
public class GameRestController extends BaseController {

    private final GameScoreService gameScoreService;

    /**
     * 게임 스코어 등록
     */
    @Operation(summary = "게임 스코어 등록", description = "게임 스코어 등록 (같은 주에 같은 사용자면 업데이트)")
    @PostMapping("/v1/scores")
    public ResponseEntity<CommonResponseDto> registerScore(
            Principal principal,
            @RequestBody GameScoreRegistrationRequestDto request) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → 내부용 Dto 변환 (생성자 직접 호출)
        GameScoreRegistrationDto registrationDto = new GameScoreRegistrationDto(
                userId,
                request.getScore(),
                request.getPetId()
        );

        gameScoreService.registerOrUpdateScore(registrationDto);

        return success();
    }

    /**
     * 주간 게임 랭킹 3위까지 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "주간 게임 랭킹 조회", description = "주간 게임 랭킹 상위 3위까지 조회")
    @GetMapping("/v1/weekly-rankings")
    public ResponseEntity<CommonResponseDto> getWeeklyTopRankings() {

        // Service에서 내부용 Dto 리스트 받기
        List<WeeklyRankingDto> rankings = gameScoreService.getWeeklyTopRankings();

        // 내부용 Dto List → ResponseDto 변환
        List<WeeklyRankingListResponseDto.RankingItemDto> rankingItems = rankings.stream()
                .map(dto -> new WeeklyRankingListResponseDto.RankingItemDto(
                        dto.getScoreId(),
                        dto.getRanking(),
                        dto.getNickname(),
                        dto.getScore(),
                        dto.getName(),
                        dto.getImageUrl()
                ))
                .collect(Collectors.toList());

        WeeklyRankingListResponseDto responseDto = new WeeklyRankingListResponseDto(rankingItems);

        return success(responseDto);
    }

    /**
     * 주간 게임 랭킹 3위까지 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "지난주 주간 게임 랭킹 조회", description = "지난주 주간 게임 랭킹 상위 3위까지 조회")
    @GetMapping("/v1/last-weekly-rankings")
    public ResponseEntity<CommonResponseDto> getLastWeeklyTopRankings() {

        // Service에서 내부용 Dto 리스트 받기
        List<WeeklyRankingDto> rankings = gameScoreService.getLastWeeklyTopRankings();

        // 내부용 Dto List → ResponseDto 변환
        List<WeeklyRankingListResponseDto.RankingItemDto> rankingItems = rankings.stream()
                .map(dto -> new WeeklyRankingListResponseDto.RankingItemDto(
                        dto.getScoreId(),
                        dto.getRanking(),
                        dto.getNickname(),
                        dto.getScore(),
                        dto.getName(),
                        dto.getImageUrl()
                ))
                .collect(Collectors.toList());

        WeeklyRankingListResponseDto responseDto = new WeeklyRankingListResponseDto(rankingItems);

        return success(responseDto);
    }

    /**
     * 주간 게임 내 랭킹 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "주간 게임 내 랭킹 조회", description = "주간 게임 내 랭킹 조회")
    @GetMapping("/v1/my-weekly-rankings")
    public ResponseEntity<CommonResponseDto> getWeeklyMyRankings(Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // Service에서 내부용 Dto 리스트 받기
        WeeklyRankingDto dto = gameScoreService.getWeeklyMyRankings(userId);


        if(dto == null){
            return success(null);
        }

        // 내부용 Dto List → ResponseDto 변환

        return success(new WeeklyRankingListResponseDto.RankingItemDto(
                dto.getScoreId(),
                dto.getRanking(),
                dto.getNickname(),
                dto.getScore(),
                dto.getName(),
                dto.getImageUrl()
        ));
    }

    /**
     * 내 주간 최대 점수 조회
     */
    @Operation(summary = "내 주간 점수 조회", description = "내 주간 최대 점수 조회")
    @GetMapping("/v1/my-weekly-score")
    public ResponseEntity<CommonResponseDto> getMyWeeklyScore(Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // Service에서 내부용 Dto 받기
        GameScoreDto scoreDto = gameScoreService.getMyWeeklyScore(userId);

        if (scoreDto == null) {
            // 점수가 없으면 null 반환
            return success(new MyWeeklyScoreResponseDto(
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }

        // 내부용 Dto → ResponseDto 변환
        MyWeeklyScoreResponseDto responseDto = new MyWeeklyScoreResponseDto(
                scoreDto.getWeekStartDate(),
                scoreDto.getScore(),
                scoreDto.getNickname(),
                scoreDto.getName(),
                scoreDto.getImageUrl()
        );

        return success(responseDto);
    }

    /**
     * 상대방 게임 점수 검색
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "상대방 게임 점수 검색", description = "상대방 게임 점수 검색")
    @GetMapping("/v1/scores/weekly/{nickname}")
    public ResponseEntity<CommonResponseDto> getWeeklyScoreByNickname(@PathVariable String nickname) {


        // Service에서 내부용 Dto 받기
        GameScoreDto scoreDto = gameScoreService.getWeeklyScoreByNickname(nickname);

        if (scoreDto == null) {
            // 점수가 없으면 null 반환
            return success(new MyWeeklyScoreResponseDto(
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }

        // 내부용 Dto → ResponseDto 변환
        MyWeeklyScoreResponseDto responseDto = new MyWeeklyScoreResponseDto(
                scoreDto.getWeekStartDate(),
                scoreDto.getScore(),
                scoreDto.getNickname(),
                scoreDto.getName(),
                scoreDto.getImageUrl()
        );

        return success(responseDto);
    }
}
