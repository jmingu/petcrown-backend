package kr.co.api.event.converter.dtoCommand;

import kr.co.api.event.dto.command.EventInfoDto;
import kr.co.api.event.dto.command.EventRegistrationDto;
import kr.co.api.event.dto.command.EventUpdateDto;
import kr.co.api.event.dto.request.EventRegistrationRequestDto;
import kr.co.api.event.dto.request.EventUpdateRequestDto;
import kr.co.api.event.dto.response.EventListResponseDto;
import kr.co.api.event.dto.response.EventResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event DTO와 Command DTO 간 양방향 변환을 담당하는 Converter
 * Request DTO -> Command DTO 변환과 Command DTO -> Response DTO 변환을 모두 담당
 */
@Component
public class EventDtoCommandConverter {

    // Request DTO -> Command DTO 변환 메서드들

    /**
     * EventRegistrationRequestDto를 EventRegistrationDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param createUserId 생성자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public EventRegistrationDto toCommandDto(EventRegistrationRequestDto request, Long createUserId) {
        if (request == null) {
            return null;
        }

        return new EventRegistrationDto(
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getStartDate(),
                request.getEndDate(),
                createUserId,
                request.getThumbnailFile(),
                request.getImageFiles()
        );
    }

    /**
     * EventUpdateRequestDto를 EventUpdateDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param updateUserId 수정자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public EventUpdateDto toCommandDto(EventUpdateRequestDto request, Long updateUserId) {
        if (request == null) {
            return null;
        }

        return new EventUpdateDto(
                request.getEventId(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getStartDate(),
                request.getEndDate(),
                updateUserId,
                request.getThumbnailFile(),
                request.getImageFiles()
        );
    }

    // Command DTO -> Response DTO 변환 메서드들

    /**
     * EventInfoDto를 EventResponseDto로 변환
     *
     * @param eventInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public EventResponseDto toResponseDto(EventInfoDto eventInfoDto) {
        if (eventInfoDto == null) {
            return null;
        }

        return new EventResponseDto(
                eventInfoDto.getEventId(),
                eventInfoDto.getTitle(),
                eventInfoDto.getContent(),
                eventInfoDto.getContentType(),
                eventInfoDto.getStartDate(),
                eventInfoDto.getEndDate(),
                eventInfoDto.getViewCount(),
                eventInfoDto.getCreateDate(),
                eventInfoDto.getCreateUserId(),
                eventInfoDto.getThumbnailUrl(),
                eventInfoDto.getImageUrls()
        );
    }

    /**
     * EventInfoDto를 EventListResponseDto로 변환 (목록용)
     *
     * @param eventInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public EventListResponseDto toListResponseDto(EventInfoDto eventInfoDto) {
        if (eventInfoDto == null) {
            return null;
        }

        return new EventListResponseDto(
                eventInfoDto.getEventId(),
                eventInfoDto.getTitle(),
                eventInfoDto.getStartDate(),
                eventInfoDto.getEndDate(),
                eventInfoDto.getViewCount(),
                eventInfoDto.getCreateDate(),
                eventInfoDto.getThumbnailUrl()
        );
    }

    /**
     * EventInfoDto 리스트를 EventListResponseDto 리스트로 변환
     *
     * @param eventInfoDtos Service Layer에서 반환된 CommandDto 리스트
     * @return HTTP 응답으로 보낼 ResponseDto 리스트
     */
    public List<EventListResponseDto> toListResponseDtos(List<EventInfoDto> eventInfoDtos) {
        if (eventInfoDtos == null) {
            return null;
        }

        return eventInfoDtos.stream()
                .map(this::toListResponseDto)
                .collect(Collectors.toList());
    }
}
