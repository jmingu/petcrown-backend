package kr.co.api.vote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.vote.converter.dtoCommand.VoteDtoCommandConverter;
import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.command.VoteListDto;
import kr.co.api.vote.dto.command.VoteRegistrationDto;
import kr.co.api.vote.dto.command.VoteUpdateDto;
import kr.co.api.vote.dto.request.VoteRegistrationRequestDto;
import kr.co.api.vote.dto.request.VoteRequestDto;
import kr.co.api.vote.dto.request.VoteUpdateRequestDto;
import kr.co.api.vote.dto.response.VoteListResponseDto;
import kr.co.api.vote.dto.response.VotePetResponseDto;
import kr.co.api.vote.service.VoteService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/votes")
@Tag(name = "Vote", description = "투표 관련 API")
@Validated
public class VoteController extends BaseController {

    private final VoteService voteService;
    private final VoteDtoCommandConverter voteDtoCommandConverter;

    /**
     * 투표 등록
     */
    @PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "투표 등록", description = "나의 펫 투표 등록")
    public ResponseEntity<CommonResponseDto> createVote(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart("data") VoteRegistrationRequestDto request,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        VoteRegistrationDto voteRegistrationDto = voteDtoCommandConverter.toCommandDto(request, userId);

        voteService.createVote(voteRegistrationDto, image);

        return success();
    }

    /**
     * 투표 목록 조회
     */
    @AuthRequired(authSkip = true)
    @GetMapping("/v1")
    @Operation(summary = "투표 목록 조회", description = "투표 목록 조회")
    public ResponseEntity<CommonResponseDto> getVotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        VoteListDto voteListDto = voteService.getVotes(page, size);
        VoteListResponseDto responseDto = voteDtoCommandConverter.toListResponseDto(voteListDto);

        return success(responseDto);
    }

    /**
     * 투표 상세 조회
     */
    @AuthRequired(authSkip = true)
    @GetMapping("/v1/{voteId}")
    @Operation(summary = "투표 상세 조회", description = "특정 투표의 상세 정보 조회")
    public ResponseEntity<CommonResponseDto> getVote(
            @PathVariable Long voteId) {


        VoteInfoDto voteInfoDto = voteService.getVote(voteId);
        VotePetResponseDto responseDto = voteDtoCommandConverter.toResponseDto(voteInfoDto);
        log.debug("responseDto {}", responseDto);
        return success(responseDto);
    }

    /**
     * 투표 수정
     */
    @PutMapping(value = "/v1/{voteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "투표 수정", description = "투표 정보 수정")
    public ResponseEntity<CommonResponseDto> updateVote(
            @PathVariable Long voteId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart("data") VoteUpdateRequestDto request,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → UpdateCommandDto 변환 (Converter 패턴 사용)
        VoteUpdateDto voteUpdateDto = voteDtoCommandConverter.toUpdateCommandDto(request, userId, voteId);

        voteService.updateVote(voteUpdateDto, image);

        return success();
    }

    /**
     * 투표 삭제
     */
    @DeleteMapping("/v1/{voteId}")
    @Operation(summary = "투표 삭제", description = "투표 삭제")
    public ResponseEntity<CommonResponseDto> deleteVote(
            @PathVariable Long voteId,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        voteService.deleteVote(voteId, userId);

        return success();
    }

    /**
     * 주간 투표하기
     */
    @AuthRequired(authSkip = true)
    @PostMapping("/v1/{voteId}/weekly")
    @Operation(summary = "주간 투표하기", description = "주간 투표에 참여하기")
    public ResponseEntity<CommonResponseDto> castVoteWeekly(
            @PathVariable Long voteId,
            @RequestBody VoteRequestDto request) {

        voteService.castVoteWeekly(voteId, request.getEmail());

        return success();
    }

    /**
     * 월간 투표하기
     */
    @AuthRequired(authSkip = true)
    @PostMapping("/v1/{voteId}/monthly")
    @Operation(summary = "월간 투표하기", description = "월간 투표에 참여하기")
    public ResponseEntity<CommonResponseDto> castVoteMonthly(
            @PathVariable Long voteId,
            @RequestBody VoteRequestDto request) {

        voteService.castVoteMonthly(voteId, request.getEmail());

        return success();
    }
}