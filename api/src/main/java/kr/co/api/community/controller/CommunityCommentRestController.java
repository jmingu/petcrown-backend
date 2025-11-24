package kr.co.api.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.community.dto.command.CommunityCommentInfoDto;
import kr.co.api.community.dto.command.CommunityCommentRegistrationDto;
import kr.co.api.community.dto.command.CommunityCommentUpdateDto;
import kr.co.api.community.dto.request.CommunityCommentRegistrationRequestDto;
import kr.co.api.community.dto.request.CommunityCommentUpdateRequestDto;
import kr.co.api.community.dto.response.CommunityCommentResponseDto;
import kr.co.api.community.service.CommunityCommentService;
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
@RequestMapping("/community/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Community Comment", description = "커뮤니티 댓글 관련 API")
public class CommunityCommentRestController extends BaseController {

    private final CommunityCommentService commentService;

    @Operation(summary = "댓글/대댓글 등록", description = "댓글 또는 대댓글 등록")
    @PostMapping("/v1")
    public ResponseEntity<CommonResponseDto> createComment(
            Principal principal, @RequestBody CommunityCommentRegistrationRequestDto request) {

        Long userId = Long.parseLong(principal.getName());

        CommunityCommentRegistrationDto commentRegistrationDto = new CommunityCommentRegistrationDto(
                request.getPostId(),
                userId,
                request.getParentCommentId(),
                request.getContent(),
                userId
        );

        commentService.createComment(commentRegistrationDto);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "게시글의 댓글 목록 조회", description = "게시글의 모든 댓글 및 대댓글 조회")
    @GetMapping("/v1/post/{postId}")
    public ResponseEntity<CommonResponseDto> getCommentsByPostId(Principal principal,@PathVariable Long postId) {

        Long userId = Long.parseLong(principal.getName());
        List<CommunityCommentInfoDto> commentInfoDtos = commentService.getCommentsByPostId(postId, userId);

        // InfoDto -> ResponseDto 변환
        List<CommunityCommentResponseDto> responseDtos = commentInfoDtos.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return success(responseDtos);
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @PutMapping("/v1/{commentId}")
    public ResponseEntity<CommonResponseDto> updateComment(
            Principal principal,
            @PathVariable Long commentId,
            @RequestBody CommunityCommentUpdateRequestDto request) {

        Long updateUserId = Long.parseLong(principal.getName());

        CommunityCommentUpdateDto commentUpdateDto = new CommunityCommentUpdateDto(
                commentId,
                request.getContent(),
                updateUserId
        );

        commentService.updateComment(commentUpdateDto);

        return success();
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제")
    @DeleteMapping("/v1/{commentId}")
    public ResponseEntity<CommonResponseDto> deleteComment(
            Principal principal, @PathVariable Long commentId) {

        Long deleteUserId = Long.parseLong(principal.getName());
        commentService.deleteComment(commentId, deleteUserId);

        return success();
    }

    @Operation(summary = "댓글 좋아요", description = "댓글 좋아요")
    @PostMapping("/v1/{commentId}/like")
    public ResponseEntity<CommonResponseDto> likeComment(@PathVariable Long commentId) {

        commentService.likeComment(commentId);

        return success();
    }

    // Helper method to convert InfoDto to ResponseDto
    private CommunityCommentResponseDto toResponseDto(CommunityCommentInfoDto infoDto) {
        List<CommunityCommentResponseDto> replyDtos = null;

        if (infoDto.getReplies() != null) {
            replyDtos = infoDto.getReplies().stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
        }

        return new CommunityCommentResponseDto(
                infoDto.getCommentId(),
                infoDto.getPostId(),
                infoDto.getNickname(),
                infoDto.getParentCommentId(),
                infoDto.getContent(),
                infoDto.getLikeCount(),
                infoDto.getDepth(),
                infoDto.getCreateDate(),
                infoDto.getCommentWriteYn(),
                replyDtos
        );
    }
}
