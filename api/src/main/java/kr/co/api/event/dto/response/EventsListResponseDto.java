package kr.co.api.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 이벤트 목록 응답 DTO (리스트 + 총 개수)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventsListResponseDto {

    private List<EventListResponseDto> events;
    private int totalCount;
}
