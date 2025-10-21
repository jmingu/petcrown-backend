package kr.co.api.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRegistrationRequestDto {

    @Schema(description = "공지사항 제목", required = true)
    private String title;

    @Schema(description = "공지사항 내용", required = true)
    private String content;

    @Schema(description = "내용 형식 (TEXT, HTML)", required = true)
    private String contentType;

    @Schema(description = "상단 고정 여부 (Y, N)", example = "N")
    private String isPinned;

    @Schema(description = "고정 순서", example = "1")
    private Integer pinOrder;

    @Schema(description = "공지 시작일", required = true)
    private LocalDateTime startDate;

    @Schema(description = "공지 종료일")
    private LocalDateTime endDate;
}