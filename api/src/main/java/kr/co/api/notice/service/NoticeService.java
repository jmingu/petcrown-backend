package kr.co.api.notice.service;

import kr.co.api.notice.domain.model.Notice;
import kr.co.api.notice.dto.command.NoticeInfoDto;
import kr.co.api.notice.dto.command.NoticeQueryDto;
import kr.co.api.notice.dto.command.NoticeRegistrationDto;
import kr.co.api.notice.dto.command.NoticeUpdateDto;
import kr.co.api.notice.repository.NoticeRepository;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 등록
     */
    @Transactional
    public void createNotice(NoticeRegistrationDto noticeRegistrationDto) {

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        Notice notice = Notice.createNotice(
                noticeRegistrationDto.getTitle(),
                noticeRegistrationDto.getContent(),
                noticeRegistrationDto.getContentType(),
                noticeRegistrationDto.getIsPinned(),
                noticeRegistrationDto.getPinOrder(),
                noticeRegistrationDto.getStartDate(),
                noticeRegistrationDto.getEndDate(),
                noticeRegistrationDto.getCreateUserId()
        );

        // 비즈니스 규칙 검증
        validateNoticeForRegistration(notice);

        // Repository에 도메인 객체 직접 전달
        Long noticeId = noticeRepository.insertNotice(notice);

        log.info("Notice created successfully: noticeId={}", noticeId);
    }

    /**
     * 공지사항 상세 조회 (조회수 증가)
     */
    @Transactional
    public NoticeInfoDto getNoticeDetail(Long noticeId) {
        NoticeQueryDto noticeQueryDto = noticeRepository.selectByNoticeId(noticeId);
        if (noticeQueryDto == null) {
            throw new PetCrownException(BusinessCode.NOTICE_NOT_FOUND);
        }

        // 조회수 증가
        noticeRepository.incrementViewCount(noticeId);

        // Dto를 CommandDto로 변환 (생성자 직접 호출)
        return convertToNoticeInfoDto(noticeQueryDto);
    }

    /**
     * 공지사항 조회 (조회수 증가 없음)
     */
    public NoticeInfoDto getNotice(Long noticeId) {
        NoticeQueryDto noticeQueryDto = noticeRepository.selectByNoticeId(noticeId);
        if (noticeQueryDto == null) {
            throw new PetCrownException(BusinessCode.NOTICE_NOT_FOUND);
        }

        return convertToNoticeInfoDto(noticeQueryDto);
    }

    /**
     * 활성화된 공지사항 목록 조회
     */
    public List<NoticeInfoDto> getActiveNotices(int page, int size) {
        int offset = (page - 1) * size;
        List<NoticeQueryDto> noticeQueryDtos = noticeRepository.selectActiveNotices(offset, size);

        return noticeQueryDtos.stream()
                .map(this::convertToNoticeInfoDto)
                .collect(Collectors.toList());
    }

    /**
     * 상단 고정 공지사항 목록 조회
     */
    public List<NoticeInfoDto> getPinnedNotices() {
        List<NoticeQueryDto> noticeQueryDtos = noticeRepository.selectPinnedNotices();

        return noticeQueryDtos.stream()
                .map(this::convertToNoticeInfoDto)
                .collect(Collectors.toList());
    }

    /**
     * 전체 공지사항 목록 조회 (관리자용)
     */
    public List<NoticeInfoDto> getAllNotices(int page, int size) {
        int offset = (page - 1) * size;
        List<NoticeQueryDto> noticeQueryDtos = noticeRepository.selectAllNotices(offset, size);

        return noticeQueryDtos.stream()
                .map(this::convertToNoticeInfoDto)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 공지사항 개수 조회
     */
    public int getActiveNoticesCount() {
        return noticeRepository.countActiveNotices();
    }

    /**
     * 전체 공지사항 개수 조회
     */
    public int getAllNoticesCount() {
        return noticeRepository.countAllNotices();
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public void updateNotice(NoticeUpdateDto noticeUpdateDto) {

        // 기존 공지사항 조회
        NoticeQueryDto existingNotice = noticeRepository.selectByNoticeId(noticeUpdateDto.getNoticeId());
        if (existingNotice == null) {
            throw new PetCrownException(BusinessCode.NOTICE_NOT_FOUND);
        }

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        Notice notice = Notice.createNotice(
                noticeUpdateDto.getTitle(),
                noticeUpdateDto.getContent(),
                noticeUpdateDto.getContentType(),
                noticeUpdateDto.getIsPinned(),
                noticeUpdateDto.getPinOrder(),
                noticeUpdateDto.getStartDate(),
                noticeUpdateDto.getEndDate(),
                noticeUpdateDto.getUpdateUserId()
        );

        // 비즈니스 규칙 검증
        validateNoticeForUpdate(notice);

        // 공지사항 정보 업데이트
        noticeRepository.updateNotice(noticeUpdateDto);

        log.info("Notice updated successfully: noticeId={}", noticeUpdateDto.getNoticeId());
    }

    /**
     * 공지사항 삭제 (논리 삭제)
     */
    @Transactional
    public void deleteNotice(Long noticeId, Long deleteUserId) {
        NoticeQueryDto existingNotice = noticeRepository.selectByNoticeId(noticeId);
        if (existingNotice == null) {
            throw new PetCrownException(BusinessCode.NOTICE_NOT_FOUND);
        }

        noticeRepository.deleteById(noticeId, deleteUserId);

        log.info("Notice deleted successfully: noticeId={}", noticeId);
    }

    /**
     * 제목으로 공지사항 검색
     */
    public List<NoticeInfoDto> searchNoticesByTitle(String title, int page, int size) {
        int offset = (page - 1) * size;
        List<NoticeQueryDto> noticeQueryDtos = noticeRepository.searchByTitle(title, offset, size);

        return noticeQueryDtos.stream()
                .map(this::convertToNoticeInfoDto)
                .collect(Collectors.toList());
    }

    /**
     * 제목으로 검색된 공지사항 개수
     */
    public int getSearchNoticesCountByTitle(String title) {
        return noticeRepository.countSearchByTitle(title);
    }

    // ========================
    // 비즈니스 로직 메서드들
    // ========================

    /**
     * 공지사항 등록 검증
     */
    private void validateNoticeForRegistration(Notice notice) {
        if (notice == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 시작일과 종료일 검증
        if (notice.getEndDate() != null && notice.getStartDate().isAfter(notice.getEndDate())) {
            throw new PetCrownException(BusinessCode.INVALID_DATE_RANGE);
        }

        // 상단 고정 공지사항의 경우 고정 순서 검증
        if (notice.isPinned() && notice.getPinOrder() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
    }

    /**
     * 공지사항 수정 검증
     */
    private void validateNoticeForUpdate(Notice notice) {
        if (notice == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 시작일과 종료일 검증
        if (notice.getEndDate() != null && notice.getStartDate().isAfter(notice.getEndDate())) {
            throw new PetCrownException(BusinessCode.INVALID_DATE_RANGE);
        }

        // 상단 고정 공지사항의 경우 고정 순서 검증
        if (notice.isPinned() && notice.getPinOrder() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
    }

    /**
     * NoticeQueryDto → NoticeInfoDto 변환 (생성자 직접 호출)
     */
    private NoticeInfoDto convertToNoticeInfoDto(NoticeQueryDto noticeQueryDto) {
        return new NoticeInfoDto(
                noticeQueryDto.getNoticeId(),
                noticeQueryDto.getTitle(),
                noticeQueryDto.getContent(),
                noticeQueryDto.getContentType(),
                noticeQueryDto.getIsPinned(),
                noticeQueryDto.getPinOrder(),
                noticeQueryDto.getStartDate(),
                noticeQueryDto.getEndDate(),
                noticeQueryDto.getViewCount(),
                noticeQueryDto.getCreateDate(),
                noticeQueryDto.getCreateUserId()
        );
    }
}