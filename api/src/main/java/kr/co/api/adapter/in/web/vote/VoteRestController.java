package kr.co.api.adapter.in.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.adapter.in.dto.vote.request.VoteRegistrationRequestDto;
import kr.co.api.application.port.in.vote.VoteUseCase;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/vote")
@Tag(name = "Vote", description = "투표 관련 API")
public class VoteRestController extends BaseController {

     private final VoteUseCase voteUseCase;

    @PostMapping("/v1")
    @Operation(summary = "투표 등록", description = "나의 펫 투표 등록")
    public ResponseEntity<CommonResponseDto> createPetVote(@RequestBody VoteRegistrationRequestDto dto, Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        voteUseCase.createVote(dto.getPetId(), userId, dto.getProfileImageUrl());

        return success();
    }
}
