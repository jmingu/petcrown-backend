package kr.co.api.event.converter.entityCommand;

import kr.co.api.event.dto.command.EventInfoDto;
import kr.co.common.entity.event.EventEntity;
import kr.co.common.entity.file.FileInfoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EventEntity를 Command DTO로 변환하는 Converter
 * Infrastructure Layer와 Application Service Layer 간의 변환을 담당
 */
@Component
public class EventEntityCommandConverter {

    /**
     * EventEntity를 EventInfoDto로 변환
     *
     * @param eventEntity Infrastructure Layer의 Entity
     * @param fileInfoEntities 이벤트와 연관된 파일 정보 리스트
     * @return Application Service Layer의 CommandDto
     */
    public EventInfoDto toEventInfoDto(EventEntity eventEntity, List<FileInfoEntity> fileInfoEntities) {
        if (eventEntity == null) {
            return null;
        }

        String thumbnailUrl = null;
        List<String> imageUrls = new ArrayList<>();

        if (fileInfoEntities != null) {
            for (FileInfoEntity fileInfo : fileInfoEntities) {
                if ("THUMBNAIL".equals(fileInfo.getFileType())) {
                    thumbnailUrl = fileInfo.getFileUrl();
                } else if ("IMAGE".equals(fileInfo.getFileType())) {
                    imageUrls.add(fileInfo.getFileUrl());
                }
            }
        }

        return new EventInfoDto(
                eventEntity.getEventId(),
                eventEntity.getTitle(),
                eventEntity.getContent(),
                eventEntity.getContentType(),
                eventEntity.getStartDate(),
                eventEntity.getEndDate(),
                eventEntity.getViewCount(),
                eventEntity.getCreateDate(),
                eventEntity.getCreateUserId(),
                thumbnailUrl,
                imageUrls
        );
    }

    /**
     * EventEntity를 EventInfoDto로 변환 (파일 정보 없이)
     *
     * @param eventEntity Infrastructure Layer의 Entity
     * @return Application Service Layer의 CommandDto
     */
    public EventInfoDto toEventInfoDto(EventEntity eventEntity) {
        return toEventInfoDto(eventEntity, null);
    }

    /**
     * EventEntity 리스트를 EventInfoDto 리스트로 변환
     *
     * @param eventEntities Infrastructure Layer의 Entity 리스트
     * @return Application Service Layer의 CommandDto 리스트
     */
    public List<EventInfoDto> toEventInfoDtos(List<EventEntity> eventEntities) {
        if (eventEntities == null) {
            return null;
        }

        return eventEntities.stream()
                .map(this::toEventInfoDto)
                .collect(Collectors.toList());
    }
}
