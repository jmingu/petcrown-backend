package kr.co.api.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.event.converter.dtoCommand.EventDtoCommandConverter;
import kr.co.api.event.dto.command.EventInfoDto;
import kr.co.api.event.dto.command.EventRegistrationDto;
import kr.co.api.event.dto.command.EventUpdateDto;
import kr.co.api.event.dto.request.EventRegistrationRequestDto;
import kr.co.api.event.dto.request.EventUpdateRequestDto;
import kr.co.api.event.dto.response.EventListResponseDto;
import kr.co.api.event.dto.response.EventResponseDto;
import kr.co.api.event.service.EventService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Event", description = "이벤트 관련 API")
public class EventRestController extends BaseController {

    private final EventService eventService;
    private final EventDtoCommandConverter eventDtoCommandConverter;

    /**
     * 이벤트 등록 (관리자만 가능)
     */
    @Operation(summary = "이벤트 등록", description = "이벤트 등록 (관리자만 가능)")
    @PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto> createEvent(
            Principal principal, @ModelAttribute EventRegistrationRequestDto request) {

        Long createUserId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        EventRegistrationDto eventRegistrationDto = eventDtoCommandConverter.toCommandDto(request, createUserId);

        eventService.createEvent(eventRegistrationDto);

        return success();
    }

    /**
     * 이벤트 상세 조회 (조회수 증가)
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "이벤트 상세 조회", description = "이벤트 상세 조회 (조회수 증가)")
    @GetMapping("/v1/{eventId}")
    public ResponseEntity<CommonResponseDto> getEventDetail(@PathVariable Long eventId) {

        EventInfoDto eventInfoDto = eventService.getEventDetail(eventId);
        EventResponseDto responseDto = eventDtoCommandConverter.toResponseDto(eventInfoDto);

        return success(responseDto);
    }

    /**
     * 활성화된 이벤트 목록 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "활성화된 이벤트 목록 조회", description = "현재 시점에서 활성화된 이벤트 목록 조회 (썸네일 포함)")
    @GetMapping("/v1")
    public ResponseEntity<CommonResponseDto> getActiveEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<EventInfoDto> eventInfoDtos = eventService.getActiveEvents(page, size);
        List<EventListResponseDto> responseDtos = eventDtoCommandConverter.toListResponseDtos(eventInfoDtos);

        return success(responseDtos);
    }

    /**
     * 전체 이벤트 목록 조회 (관리자용)
     */
    @Operation(summary = "전체 이벤트 목록 조회", description = "전체 이벤트 목록 조회 (관리자용)")
    @GetMapping("/v1/admin")
    public ResponseEntity<CommonResponseDto> getAllEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<EventInfoDto> eventInfoDtos = eventService.getAllEvents(page, size);
        List<EventListResponseDto> responseDtos = eventDtoCommandConverter.toListResponseDtos(eventInfoDtos);

        return success(responseDtos);
    }

    /**
     * 활성화된 이벤트 개수 조회
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "활성화된 이벤트 개수 조회", description = "현재 시점에서 활성화된 이벤트 개수 조회")
    @GetMapping("/v1/count")
    public ResponseEntity<CommonResponseDto> getActiveEventsCount() {

        int count = eventService.getActiveEventsCount();

        return success(count);
    }

    /**
     * 전체 이벤트 개수 조회 (관리자용)
     */
    @Operation(summary = "전체 이벤트 개수 조회", description = "전체 이벤트 개수 조회 (관리자용)")
    @GetMapping("/v1/admin/count")
    public ResponseEntity<CommonResponseDto> getAllEventsCount() {

        int count = eventService.getAllEventsCount();

        return success(count);
    }

    /**
     * 이벤트 수정 (관리자만 가능)
     */
    @Operation(summary = "이벤트 수정", description = "이벤트 수정 (관리자만 가능)")
    @PutMapping(value = "/v1/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto> updateEvent(
            Principal principal,
            @PathVariable Long eventId,
            @ModelAttribute EventUpdateRequestDto request) {

        Long updateUserId = Long.parseLong(principal.getName());

        // eventId 설정
        request = new EventUpdateRequestDto(
                eventId,
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getThumbnailFile(),
                request.getImageFiles()
        );

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        EventUpdateDto eventUpdateDto = eventDtoCommandConverter.toCommandDto(request, updateUserId);

        eventService.updateEvent(eventUpdateDto);

        return success();
    }

    /**
     * 이벤트 삭제 (관리자만 가능)
     */
    @Operation(summary = "이벤트 삭제", description = "이벤트 삭제 (관리자만 가능)")
    @DeleteMapping("/v1/{eventId}")
    public ResponseEntity<CommonResponseDto> deleteEvent(
            Principal principal, @PathVariable Long eventId) {

        Long deleteUserId = Long.parseLong(principal.getName());

        eventService.deleteEvent(eventId, deleteUserId);

        return success();
    }

    /**
     * 제목으로 이벤트 검색
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "제목으로 이벤트 검색", description = "제목으로 이벤트 검색")
    @GetMapping("/v1/search")
    public ResponseEntity<CommonResponseDto> searchEventsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<EventInfoDto> eventInfoDtos = eventService.searchEventsByTitle(title, page, size);
        List<EventListResponseDto> responseDtos = eventDtoCommandConverter.toListResponseDtos(eventInfoDtos);

        return success(responseDtos);
    }

    /**
     * 제목으로 검색된 이벤트 개수
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "제목으로 검색된 이벤트 개수", description = "제목으로 검색된 이벤트 개수")
    @GetMapping("/v1/search/count")
    public ResponseEntity<CommonResponseDto> getSearchEventsCountByTitle(@RequestParam String title) {

        int count = eventService.getSearchEventsCountByTitle(title);

        return success(count);
    }
}
