package kr.co.api.notice.converter.entityCommand;

import kr.co.api.notice.dto.command.NoticeInfoDto;
import kr.co.common.entity.notice.NoticeEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * NoticeEntity를 Command DTO로 변환하는 Converter
 * Infrastructure Layer와 Application Service Layer 간의 변환을 담당
 */
@Component
public class NoticeEntityCommandConverter {

    /**
     * NoticeEntity를 NoticeInfoDto로 변환
     *
     * @param noticeEntity Infrastructure Layer의 Entity
     * @return Application Service Layer의 CommandDto
     */
    public NoticeInfoDto toNoticeInfoDto(NoticeEntity noticeEntity) {
        if (noticeEntity == null) {
            return null;
        }

        return new NoticeInfoDto(
                noticeEntity.getNoticeId(),
                noticeEntity.getTitle(),
                noticeEntity.getContent(),
                noticeEntity.getContentType(),
                noticeEntity.getIsPinned(),
                noticeEntity.getPinOrder(),
                noticeEntity.getStartDate(),
                noticeEntity.getEndDate(),
                noticeEntity.getViewCount(),
                noticeEntity.getCreateDate(),
                noticeEntity.getCreateUserId()
        );
    }

    /**
     * NoticeEntity 리스트를 NoticeInfoDto 리스트로 변환
     *
     * @param noticeEntities Infrastructure Layer의 Entity 리스트
     * @return Application Service Layer의 CommandDto 리스트
     */
    public List<NoticeInfoDto> toNoticeInfoDtos(List<NoticeEntity> noticeEntities) {
        if (noticeEntities == null) {
            return null;
        }

        return noticeEntities.stream()
                .map(this::toNoticeInfoDto)
                .collect(Collectors.toList());
    }
}