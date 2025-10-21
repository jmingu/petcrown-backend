package kr.co.api.notice.converter.domainEntity;

import kr.co.api.notice.domain.model.Notice;
import kr.co.api.notice.domain.vo.Content;
import kr.co.api.notice.domain.vo.ContentType;
import kr.co.api.notice.domain.vo.Title;
import kr.co.common.entity.notice.NoticeEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static kr.co.common.constant.DefaultValues.DEFAULT_ID;

/**
 * Notice 도메인과 Entity 간 변환을 담당하는 Converter
 */
@Component
public class NoticeDomainEntityConverter {

    /**
     * 공지사항 생성용 Notice 도메인 → NoticeEntity 변환
     */
    public NoticeEntity toNoticeEntityForRegistration(Notice notice) {
        if (notice == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();

        return new NoticeEntity(
                notice.getNoticeId(),
                notice.getTitle().getValue(),
                notice.getContent().getValue(),
                notice.getContentType().getValue(),
                notice.getIsPinned(),
                notice.getPinOrder(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getViewCount(),
                now,
                notice.getCreateUserId(),
                now,
                notice.getCreateUserId(),
                null,
                null
        );
    }

    /**
     * 공지사항 수정용 Notice 도메인 → NoticeEntity 변환
     */
    public NoticeEntity toNoticeEntityForUpdate(Notice notice, Long updateUserId) {
        if (notice == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();

        return new NoticeEntity(
                notice.getNoticeId(),
                notice.getTitle().getValue(),
                notice.getContent().getValue(),
                notice.getContentType().getValue(),
                notice.getIsPinned(),
                notice.getPinOrder(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getViewCount(),
                null, // createDate는 업데이트 시 변경하지 않음
                null, // createUserId는 업데이트 시 변경하지 않음
                now,
                updateUserId,
                null,
                null
        );
    }

    /**
     * NoticeEntity → Notice 도메인 변환
     */
    public Notice toNoticeDomain(NoticeEntity entity) {
        if (entity == null) {
            return null;
        }

        return Notice.getAllFields(
                entity.getNoticeId(),
                Title.of(entity.getTitle()),
                Content.of(entity.getContent()),
                ContentType.of(entity.getContentType()),
                entity.getIsPinned(),
                entity.getPinOrder(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getViewCount(),
                entity.getCreateUserId()
        );
    }
}