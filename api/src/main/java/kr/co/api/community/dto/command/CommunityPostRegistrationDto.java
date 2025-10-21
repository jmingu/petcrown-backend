package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityPostRegistrationDto {

    private final Long userId;
    private final String category;
    private final String title;
    private final String content;
    private final String contentType;
    private final Long createUserId;
    private final List<MultipartFile> imageFiles;
}
