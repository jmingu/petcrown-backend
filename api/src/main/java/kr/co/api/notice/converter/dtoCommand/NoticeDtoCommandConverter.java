package kr.co.api.notice.converter.dtoCommand;

import kr.co.api.notice.dto.command.NoticeInfoDto;
import kr.co.api.notice.dto.command.NoticeRegistrationDto;
import kr.co.api.notice.dto.command.NoticeUpdateDto;
import kr.co.api.notice.dto.request.NoticeRegistrationRequestDto;
import kr.co.api.notice.dto.request.NoticeUpdateRequestDto;
import kr.co.api.notice.dto.response.NoticeListResponseDto;
import kr.co.api.notice.dto.response.NoticeResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notice DTO와 Command DTO 간 양방향 변환을 담당하는 Converter
 * Request DTO -> Command DTO 변환과 Command DTO -> Response DTO 변환을 모두 담당
 */
@Component
public class NoticeDtoCommandConverter {

    // Request DTO -> Command DTO 변환 메서드들

    /**
     * NoticeRegistrationRequestDto를 NoticeRegistrationDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param createUserId 생성자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public NoticeRegistrationDto toCommandDto(NoticeRegistrationRequestDto request, Long createUserId) {
        if (request == null) {
            return null;
        }

        return new NoticeRegistrationDto(
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                request.getStartDate(),
                request.getEndDate(),
                createUserId
        );
    }

    /**
     * NoticeUpdateRequestDto를 NoticeUpdateDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param updateUserId 수정자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public NoticeUpdateDto toCommandDto(NoticeUpdateRequestDto request, Long updateUserId) {
        if (request == null) {
            return null;
        }

        return new NoticeUpdateDto(
                request.getNoticeId(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                request.getStartDate(),
                request.getEndDate(),
                updateUserId
        );
    }

    // Command DTO -> Response DTO 변환 메서드들

    /**
     * NoticeInfoDto를 NoticeResponseDto로 변환
     *
     * @param noticeInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public NoticeResponseDto toResponseDto(NoticeInfoDto noticeInfoDto) {
        if (noticeInfoDto == null) {
            return null;
        }

        return new NoticeResponseDto(
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
    }

    /**
     * NoticeInfoDto를 NoticeListResponseDto로 변환 (목록용)
     *
     * @param noticeInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public NoticeListResponseDto toListResponseDto(NoticeInfoDto noticeInfoDto) {
        if (noticeInfoDto == null) {
            return null;
        }

        return new NoticeListResponseDto(
                noticeInfoDto.getNoticeId(),
                noticeInfoDto.getTitle(),
                noticeInfoDto.getIsPinned(),
                noticeInfoDto.getPinOrder(),
                noticeInfoDto.getStartDate(),
                noticeInfoDto.getEndDate(),
                noticeInfoDto.getViewCount(),
                noticeInfoDto.getCreateDate()
        );
    }

    /**
     * NoticeInfoDto 리스트를 NoticeListResponseDto 리스트로 변환
     *
     * @param noticeInfoDtos Service Layer에서 반환된 CommandDto 리스트
     * @return HTTP 응답으로 보낼 ResponseDto 리스트
     */
    public List<NoticeListResponseDto> toListResponseDtos(List<NoticeInfoDto> noticeInfoDtos) {
        if (noticeInfoDtos == null) {
            return null;
        }

        return noticeInfoDtos.stream()
                .map(this::toListResponseDto)
                .collect(Collectors.toList());
    }
}