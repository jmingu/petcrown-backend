package kr.co.api.notice.converter.dtoDomain;

import kr.co.api.notice.domain.model.Notice;
import kr.co.api.notice.dto.command.NoticeRegistrationDto;
import kr.co.api.notice.dto.command.NoticeUpdateDto;
import org.springframework.stereotype.Component;

/**
 * Notice Command DTO와 Domain 객체 간 변환을 담당하는 Converter
 */
@Component
public class NoticeDtoDomainConverter {

    /**
     * 공지사항 생성용 Notice 도메인 객체 생성
     *
     * @param dto 공지사항 등록 CommandDto
     * @return Notice 도메인 객체
     */
    public Notice toNoticeForRegistration(NoticeRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        return Notice.createNotice(
                dto.getTitle(),
                dto.getContent(),
                dto.getContentType(),
                dto.getIsPinned(),
                dto.getPinOrder(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getCreateUserId()
        );
    }

    /**
     * 공지사항 수정용 Notice 도메인 객체 생성
     *
     * @param dto 공지사항 수정 CommandDto
     * @return Notice 도메인 객체
     */
    public Notice toNoticeForUpdate(NoticeUpdateDto dto) {
        if (dto == null) {
            return null;
        }

        return Notice.createNotice(
                dto.getTitle(),
                dto.getContent(),
                dto.getContentType(),
                dto.getIsPinned(),
                dto.getPinOrder(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getUpdateUserId()
        );
    }
}