package kr.co.api.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.notice.dto.command.NoticeInfoDto;
import kr.co.api.notice.dto.command.NoticeRegistrationDto;
import kr.co.api.notice.dto.command.NoticeUpdateDto;
import kr.co.api.notice.dto.request.NoticeRegistrationRequestDto;
import kr.co.api.notice.dto.request.NoticeUpdateRequestDto;
import kr.co.api.notice.dto.response.NoticeListResponseDto;
import kr.co.api.notice.dto.response.NoticesListResponseDto;
import kr.co.api.notice.dto.response.NoticeResponseDto;
import kr.co.api.notice.service.NoticeService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notice", description = "공지사항 관련 API")
public class NoticeRestController extends BaseController {

    private final NoticeService noticeService;

    /**
     * 공지사항 등록 (관리자만 가능)
     */
    @Operation(summary = "공지사항 등록", description = "공지사항 등록 (관리자만 가능)")
    @PostMapping("/v1")
    public ResponseEntity<CommonResponseDto> createNotice(
            Principal principal, @RequestBody NoticeRegistrationRequestDto request) {

        Long createUserId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (생성자 직접 호출)
        NoticeRegistrationDto noticeRegistrationDto = new NoticeRegistrationDto(
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                request.getStartDate(),
                request.getEndDate(),
                createUserId
        );

        noticeService.createNotice(noticeRegistrationDto);

        return success();
    }

    /**
     * 공지사항 상세 조회 (조회수 증가)
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세 조회 (조회수 증가)")
    @GetMapping("/v1/{noticeId}")
    public ResponseEntity<CommonResponseDto> getNoticeDetail(@PathVariable Long noticeId) {

        NoticeInfoDto noticeInfoDto = noticeService.getNoticeDetail(noticeId);

        // CommandDto → ResponseDto 변환 (생성자 직접 호출)
        NoticeResponseDto responseDto = new NoticeResponseDto(
                noticeInfoDto.getNoticeId(),
                noticeInfoDto.getTitle(),
                noticeInfoDto.getContent(),
                noticeInfoDto.getContentType(),
                noticeInfoDto.getIsPinned(),
                noticeInfoDto.getPinOrder(),
                noticeInfoDto.getStartDate(),
                noticeInfoDto.getEndDate(),
                noticeInfoDto.getViewCount(),
                noticeInfoDto.getCreateDate(),
                noticeInfoDto.getCreateUserId()
        );

        return success(responseDto);
    }

    /**
     * 활성화된 공지사항 목록 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "활성화된 공지사항 목록 조회", description = "현재 시점에서 활성화된 공지사항 목록 조회 (리스트 + 총 개수)")
    @GetMapping("/v1")
    public ResponseEntity<CommonResponseDto> getActiveNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<NoticeInfoDto> noticeInfoDtos = noticeService.getActiveNotices(page, size);
        int totalCount = noticeService.getActiveNoticesCount();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<NoticeListResponseDto> notices = noticeInfoDtos.stream()
                .map(dto -> new NoticeListResponseDto(
                        dto.getNoticeId(),
                        dto.getTitle(),
                        dto.getIsPinned(),
                        dto.getPinOrder(),
                        dto.getStartDate(),
                        dto.getEndDate(),
                        dto.getViewCount(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        // 리스트 + 총 개수를 감싸는 ResponseDto 생성
        NoticesListResponseDto responseDto = new NoticesListResponseDto(notices, totalCount);

        return success(responseDto);
    }

    /**
     * 상단 고정 공지사항 목록 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "상단 고정 공지사항 목록 조회", description = "상단에 고정된 공지사항 목록 조회 (리스트 + 총 개수)")
    @GetMapping("/v1/pinned")
    public ResponseEntity<CommonResponseDto> getPinnedNotices() {

        List<NoticeInfoDto> noticeInfoDtos = noticeService.getPinnedNotices();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<NoticeListResponseDto> notices = noticeInfoDtos.stream()
                .map(dto -> new NoticeListResponseDto(
                        dto.getNoticeId(),
                        dto.getTitle(),
                        dto.getIsPinned(),
                        dto.getPinOrder(),
                        dto.getStartDate(),
                        dto.getEndDate(),
                        dto.getViewCount(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        // 리스트 + 총 개수를 감싸는 ResponseDto 생성
        NoticesListResponseDto responseDto = new NoticesListResponseDto(notices, notices.size());

        return success(responseDto);
    }

    /**
     * 전체 공지사항 목록 조회 (관리자용)
     */
    @Operation(summary = "전체 공지사항 목록 조회", description = "전체 공지사항 목록 조회 (관리자용, 리스트 + 총 개수)")
    @GetMapping("/v1/admin")
    public ResponseEntity<CommonResponseDto> getAllNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<NoticeInfoDto> noticeInfoDtos = noticeService.getAllNotices(page, size);
        int totalCount = noticeService.getAllNoticesCount();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<NoticeListResponseDto> notices = noticeInfoDtos.stream()
                .map(dto -> new NoticeListResponseDto(
                        dto.getNoticeId(),
                        dto.getTitle(),
                        dto.getIsPinned(),
                        dto.getPinOrder(),
                        dto.getStartDate(),
                        dto.getEndDate(),
                        dto.getViewCount(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        // 리스트 + 총 개수를 감싸는 ResponseDto 생성
        NoticesListResponseDto responseDto = new NoticesListResponseDto(notices, totalCount);

        return success(responseDto);
    }

    /**
     * 활성화된 공지사항 개수 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "활성화된 공지사항 개수 조회", description = "현재 시점에서 활성화된 공지사항 개수 조회")
    @GetMapping("/v1/count")
    public ResponseEntity<CommonResponseDto> getActiveNoticesCount() {

        int count = noticeService.getActiveNoticesCount();

        return success(count);
    }

    /**
     * 전체 공지사항 개수 조회 (관리자용)
     */
    @Operation(summary = "전체 공지사항 개수 조회", description = "전체 공지사항 개수 조회 (관리자용)")
    @GetMapping("/v1/admin/count")
    public ResponseEntity<CommonResponseDto> getAllNoticesCount() {

        int count = noticeService.getAllNoticesCount();

        return success(count);
    }

    /**
     * 공지사항 수정 (관리자만 가능)
     */
    @Operation(summary = "공지사항 수정", description = "공지사항 수정 (관리자만 가능)")
    @PutMapping("/v1/{noticeId}")
    public ResponseEntity<CommonResponseDto> updateNotice(
            Principal principal,
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequestDto request) {

        Long updateUserId = Long.parseLong(principal.getName());

        // noticeId 설정
        request = new NoticeUpdateRequestDto(
                noticeId,
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                request.getStartDate(),
                request.getEndDate()
        );

        // RequestDto → CommandDto 변환 (생성자 직접 호출)
        NoticeUpdateDto noticeUpdateDto = new NoticeUpdateDto(
                noticeId,
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                request.getStartDate(),
                request.getEndDate(),
                updateUserId
        );

        noticeService.updateNotice(noticeUpdateDto);

        return success();
    }

    /**
     * 공지사항 삭제 (관리자만 가능)
     */
    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제 (관리자만 가능)")
    @DeleteMapping("/v1/{noticeId}")
    public ResponseEntity<CommonResponseDto> deleteNotice(
            Principal principal, @PathVariable Long noticeId) {

        Long deleteUserId = Long.parseLong(principal.getName());

        noticeService.deleteNotice(noticeId, deleteUserId);

        return success();
    }

    /**
     * 제목으로 공지사항 검색
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "제목으로 공지사항 검색", description = "제목으로 공지사항 검색 (리스트 + 총 개수)")
    @GetMapping("/v1/search")
    public ResponseEntity<CommonResponseDto> searchNoticesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<NoticeInfoDto> noticeInfoDtos = noticeService.searchNoticesByTitle(title, page, size);
        int totalCount = noticeService.getSearchNoticesCountByTitle(title);

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<NoticeListResponseDto> notices = noticeInfoDtos.stream()
                .map(dto -> new NoticeListResponseDto(
                        dto.getNoticeId(),
                        dto.getTitle(),
                        dto.getIsPinned(),
                        dto.getPinOrder(),
                        dto.getStartDate(),
                        dto.getEndDate(),
                        dto.getViewCount(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        // 리스트 + 총 개수를 감싸는 ResponseDto 생성
        NoticesListResponseDto responseDto = new NoticesListResponseDto(notices, totalCount);

        return success(responseDto);
    }

    /**
     * 제목으로 검색된 공지사항 개수
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "제목으로 검색된 공지사항 개수", description = "제목으로 검색된 공지사항 개수")
    @GetMapping("/v1/search/count")
    public ResponseEntity<CommonResponseDto> getSearchNoticesCountByTitle(@RequestParam String title) {

        int count = noticeService.getSearchNoticesCountByTitle(title);

        return success(count);
    }
}