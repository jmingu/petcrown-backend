package kr.co.api.notice.domain.model;

import kr.co.api.notice.domain.vo.Content;
import kr.co.api.notice.domain.vo.ContentType;
import kr.co.api.notice.domain.vo.Title;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Notice {

    private final Long noticeId;
    private final Title title;
    private final Content content;
    private final ContentType contentType;
    private final String isPinned;
    private final Integer pinOrder;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long viewCount;
    private final Long createUserId;

    /**
     * ID로만 공지사항 생성 (최소 정보)
     */
    public static Notice ofId(Long noticeId) {
        if (noticeId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new Notice(noticeId, null, null, null, null, null, null, null, null, null);
    }

    /**
     * 새 공지사항 생성
     */
    public static Notice createNotice(String titleValue, String contentValue, String contentTypeValue,
                                    String isPinned, Integer pinOrder, LocalDateTime startDate,
                                    LocalDateTime endDate, Long createUserId) {

        // Value Objects 생성 (유효성 검증 포함)
        Title title = Title.of(titleValue);
        Content content = Content.of(contentValue);
        ContentType contentType = ContentType.of(contentTypeValue);

        // 기본값 설정
        String pinnedFlag = isPinned != null ? isPinned : "N";
        Integer order = pinOrder != null ? pinOrder : 1;
        Long viewCount = 0L;

        // 시작일이 없으면 현재 시간으로 설정
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now();

        return new Notice(null, title, content, contentType, pinnedFlag, order, start, endDate, viewCount, createUserId);
    }

    /**
     * 모든 필드로 공지사항 생성
     */
    public static Notice getAllFields(Long noticeId, Title title, Content content, ContentType contentType,
                                    String isPinned, Integer pinOrder, LocalDateTime startDate,
                                    LocalDateTime endDate, Long viewCount, Long createUserId) {
        return new Notice(noticeId, title, content, contentType, isPinned, pinOrder,
                         startDate, endDate, viewCount, createUserId);
    }

    /**
     * 조회수 증가
     */
    public Notice incrementViewCount() {
        Long newViewCount = this.viewCount != null ? this.viewCount + 1 : 1L;
        return new Notice(this.noticeId, this.title, this.content, this.contentType,
                         this.isPinned, this.pinOrder, this.startDate, this.endDate,
                         newViewCount, this.createUserId);
    }

    /**
     * 공지사항이 현재 활성화되어 있는지 확인
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();

        // 시작일이 지났는지 확인
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }

        // 종료일이 있고 지났는지 확인
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }

        return true;
    }

    /**
     * 상단 고정 공지사항인지 확인
     */
    public boolean isPinned() {
        return "Y".equals(this.isPinned);
    }

    /**
     * ID가 설정된 새 객체 반환 (불변성 유지)
     */
    public Notice withId(Long noticeId) {
        return new Notice(
            noticeId,
            this.title,
            this.content,
            this.contentType,
            this.isPinned,
            this.pinOrder,
            this.startDate,
            this.endDate,
            this.viewCount,
            this.createUserId
        );
    }
}