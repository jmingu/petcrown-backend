package kr.co.api.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationRequestDto {

    @Schema(description = "이벤트 제목", required = true)
    private String title;

    @Schema(description = "이벤트 내용", required = true)
    private String content;

    @Schema(description = "내용 형식 (TEXT, HTML)", required = true)
    private String contentType;

    @Schema(description = "이벤트 시작일", required = true)
    private LocalDateTime startDate;

    @Schema(description = "이벤트 종료일")
    private LocalDateTime endDate;

    @Schema(description = "썸네일 이미지")
    private MultipartFile thumbnailFile;

    @Schema(description = "이벤트 이미지 목록")
    private List<MultipartFile> imageFiles;
}
