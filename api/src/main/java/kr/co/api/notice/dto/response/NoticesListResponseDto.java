package kr.co.api.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 공지사항 목록 응답 DTO (리스트 + 총 개수)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticesListResponseDto {

    private List<NoticeListResponseDto> notices;
    private int totalCount;
}
