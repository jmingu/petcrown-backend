package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityPostUpdateDto {

    private final Long postId;
    private final String category;
    private final String title;
    private final String content;
    private final String contentType;
    private final String isPinned;
    private final Integer pinOrder;
    private final Long updateUserId;
    private final List<MultipartFile> imageFiles;
}
