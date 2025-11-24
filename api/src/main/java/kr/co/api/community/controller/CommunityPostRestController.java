package kr.co.api.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.community.dto.command.CommunityPostInfoDtailDto;
import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.api.community.dto.request.CommunityPostRegistrationRequestDto;
import kr.co.api.community.dto.request.CommunityPostUpdateRequestDto;
import kr.co.api.community.dto.response.CommunityPostListResponseDto;
import kr.co.api.community.dto.response.CommunityPostsListResponseDto;
import kr.co.api.community.dto.response.CommunityPostResponseDetailDto;
import kr.co.api.community.service.CommunityPostService;
import kr.co.common.contoller.BaseController;
import kr.co.common.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Community Post", description = "커뮤니티 게시글 관련 API")
public class CommunityPostRestController extends BaseController {

    private final CommunityPostService postService;

    @Operation(summary = "게시글 등록", description = "커뮤니티 게시글 등록")
    @PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto> createPost(
            Principal principal,
            @RequestPart("data") CommunityPostRegistrationRequestDto request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (생성자 직접 호출)
        CommunityPostRegistrationDto postRegistrationDto = new CommunityPostRegistrationDto(
                userId,
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                userId,  // createUserId
                images
        );

        postService.createPost(postRegistrationDto);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 조회 (조회수 증가)")
    @GetMapping("/v1/{postId}")
    public ResponseEntity<CommonResponseDto> getPostDetail(Principal principal, @PathVariable Long postId) {

        Long userId = Long.parseLong(principal.getName());

        CommunityPostInfoDtailDto postInfoDto = postService.getPostDetail(postId, userId);

        // CommandDto → ResponseDto 변환 (생성자 직접 호출)
        CommunityPostResponseDetailDto responseDto = new CommunityPostResponseDetailDto(
                postInfoDto.getPostId(),
                postInfoDto.getNickname(),
                postInfoDto.getCategory(),
                postInfoDto.getTitle(),
                postInfoDto.getContent(),
                postInfoDto.getContentType(),
                postInfoDto.getViewCount(),
                postInfoDto.getLikeCount(),
                postInfoDto.getCommentCount(),
                postInfoDto.getIsPinned(),
                postInfoDto.getPinOrder(),
                postInfoDto.getCreateDate(),
                postInfoDto.getImageUrls(),
                postInfoDto.getPostWriteYn()
        );

        return success(responseDto);
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "게시글 목록 조회", description = "커뮤니티 게시글 목록 조회 (리스트 + 총 개수)")
    @GetMapping("/v1")
    public ResponseEntity<CommonResponseDto> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CommunityPostInfoDto> postInfoDtos = postService.getAllPosts(page, size);
        int totalCount = postService.getAllPostsCount();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<CommunityPostListResponseDto> posts = postInfoDtos.stream()
                .map(dto -> new CommunityPostListResponseDto(
                        dto.getPostId(),
                        dto.getNickname(),
                        dto.getCategory(),
                        dto.getTitle(),
                        dto.getViewCount(),
                        dto.getLikeCount(),
                        dto.getCommentCount(),
                        dto.getIsPinned(),
                        dto.getPinOrder(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        // 리스트 + 총 개수를 감싸는 ResponseDto 생성
        CommunityPostsListResponseDto responseDto = new CommunityPostsListResponseDto(posts, totalCount);

        return success(responseDto);
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
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        Long updateUserId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (생성자 직접 호출)
        CommunityPostUpdateDto postUpdateDto = new CommunityPostUpdateDto(
                postId,
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                updateUserId,
                images
        );

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

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<CommunityPostListResponseDto> responseDtos = postInfoDtos.stream()
                .map(dto -> new CommunityPostListResponseDto(
                        dto.getPostId(),
                        dto.getNickname(),
                        dto.getCategory(),
                        dto.getTitle(),
                        dto.getViewCount(),
                        dto.getLikeCount(),
                        dto.getCommentCount(),
                        dto.getIsPinned(),
                        dto.getPinOrder(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        return success(responseDtos);
    }
}
