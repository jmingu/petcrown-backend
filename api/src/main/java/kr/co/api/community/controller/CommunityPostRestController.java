package kr.co.api.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.community.converter.dtoCommand.CommunityPostDtoCommandConverter;
import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.api.community.dto.request.CommunityPostRegistrationRequestDto;
import kr.co.api.community.dto.request.CommunityPostUpdateRequestDto;
import kr.co.api.community.dto.response.CommunityPostListResponseDto;
import kr.co.api.community.dto.response.CommunityPostResponseDto;
import kr.co.api.community.service.CommunityPostService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/community/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Community Post", description = "커뮤니티 게시글 관련 API")
public class CommunityPostRestController extends BaseController {

    private final CommunityPostService postService;
    private final CommunityPostDtoCommandConverter postDtoCommandConverter;

    @Operation(summary = "게시글 등록", description = "커뮤니티 게시글 등록")
    @PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto> createPost(
            Principal principal,
            @RequestPart("data") CommunityPostRegistrationRequestDto request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {

        Long userId = Long.parseLong(principal.getName());

        // Request DTO에 파일 설정
        CommunityPostRegistrationRequestDto requestWithFiles = new CommunityPostRegistrationRequestDto(
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                imageFiles
        );

        CommunityPostRegistrationDto postRegistrationDto = postDtoCommandConverter.toCommandDto(requestWithFiles, userId, userId);

        postService.createPost(postRegistrationDto);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 조회 (조회수 증가)")
    @GetMapping("/v1/{postId}")
    public ResponseEntity<CommonResponseDto> getPostDetail(@PathVariable Long postId) {

        CommunityPostInfoDto postInfoDto = postService.getPostDetail(postId);
        CommunityPostResponseDto responseDto = postDtoCommandConverter.toResponseDto(postInfoDto);

        return success(responseDto);
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "게시글 목록 조회", description = "커뮤니티 게시글 목록 조회")
    @GetMapping("/v1")
    public ResponseEntity<CommonResponseDto> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CommunityPostInfoDto> postInfoDtos = postService.getAllPosts(page, size);
        List<CommunityPostListResponseDto> responseDtos = postDtoCommandConverter.toListResponseDtos(postInfoDtos);

        return success(responseDtos);
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "게시글 개수 조회", description = "전체 게시글 개수 조회")
    @GetMapping("/v1/count")
    public ResponseEntity<CommonResponseDto> getAllPostsCount() {

        int count = postService.getAllPostsCount();

        return success(count);
    }

    @Operation(summary = "게시글 수정", description = "게시글 수정")
    @PutMapping(value = "/v1/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto> updatePost(
            Principal principal,
            @PathVariable Long postId,
            @RequestPart("data") CommunityPostUpdateRequestDto request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {

        Long updateUserId = Long.parseLong(principal.getName());

        // Request DTO에 파일 및 postId 설정
        CommunityPostUpdateRequestDto requestWithFiles = new CommunityPostUpdateRequestDto(
                postId,
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                imageFiles
        );

        CommunityPostUpdateDto postUpdateDto = postDtoCommandConverter.toCommandDto(requestWithFiles, updateUserId);
        postService.updatePost(postUpdateDto);

        return success();
    }

    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
    @DeleteMapping("/v1/{postId}")
    public ResponseEntity<CommonResponseDto> deletePost(
            Principal principal, @PathVariable Long postId) {

        Long deleteUserId = Long.parseLong(principal.getName());
        postService.deletePost(postId, deleteUserId);

        return success();
    }

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요")
    @PostMapping("/v1/{postId}/like")
    public ResponseEntity<CommonResponseDto> likePost(@PathVariable Long postId) {

        postService.likePost(postId);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "제목으로 게시글 검색", description = "제목으로 게시글 검색")
    @GetMapping("/v1/search")
    public ResponseEntity<CommonResponseDto> searchPostsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CommunityPostInfoDto> postInfoDtos = postService.searchPostsByTitle(title, page, size);
        List<CommunityPostListResponseDto> responseDtos = postDtoCommandConverter.toListResponseDtos(postInfoDtos);

        return success(responseDtos);
    }
}
