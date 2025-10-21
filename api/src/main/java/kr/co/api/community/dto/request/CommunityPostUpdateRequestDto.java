package kr.co.api.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostUpdateRequestDto {

    @Schema(description = "게시글 ID", required = true)
    private Long postId;

    @Schema(description = "카테고리", required = true)
    private String category;

    @Schema(description = "게시글 제목", required = true)
    private String title;

    @Schema(description = "게시글 내용", required = true)
    private String content;

    @Schema(description = "내용 형식 (TEXT, HTML)", required = true)
    private String contentType;

    @Schema(description = "상단 고정 여부 (Y, N)", example = "N")
    private String isPinned;

    @Schema(description = "고정 순서", example = "1")
    private Integer pinOrder;

    @Schema(description = "첨부 이미지 목록")
    private List<MultipartFile> imageFiles;
}
