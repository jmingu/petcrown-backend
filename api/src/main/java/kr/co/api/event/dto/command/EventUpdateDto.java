package kr.co.api.event.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class EventUpdateDto {

    private final Long eventId;
    private final String title;
    private final String content;
    private final String contentType;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long updateUserId;
    private final MultipartFile thumbnailFile;
    private final List<MultipartFile> imageFiles;
}
