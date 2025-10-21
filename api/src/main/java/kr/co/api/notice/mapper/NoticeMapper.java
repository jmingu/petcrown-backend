package kr.co.api.notice.mapper;

import kr.co.api.notice.dto.command.NoticeUpdateDto;
import kr.co.common.entity.notice.NoticeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    /**
     * 공지사항 저장
     */
    void insertNotice(NoticeEntity notice);

    /**
     * 공지사항 ID로 조회
     */
    NoticeEntity selectByNoticeId(@Param("noticeId") Long noticeId);

    /**
     * 활성화된 공지사항 목록 조회 (페이징)
     */
    List<NoticeEntity> selectActiveNotices(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 상단 고정 공지사항 목록 조회
     */
    List<NoticeEntity> selectPinnedNotices();

    /**
     * 전체 공지사항 목록 조회 (관리자용)
     */
    List<NoticeEntity> selectAllNotices(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 활성화된 공지사항 개수 조회
     */
    int countActiveNotices();

    /**
     * 전체 공지사항 개수 조회
     */
    int countAllNotices();

    /**
     * 공지사항 수정
     */
    void updateNotice(NoticeUpdateDto noticeUpdateDto);

    /**
     * 조회수 증가
     */
    void incrementViewCount(@Param("noticeId") Long noticeId);

    /**
     * 공지사항 삭제 (논리 삭제)
     */
    void deleteById(@Param("noticeId") Long noticeId, @Param("deleteUserId") Long deleteUserId);

    /**
     * 제목으로 공지사항 검색
     */
    List<NoticeEntity> searchByTitle(@Param("title") String title, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 제목으로 검색된 공지사항 개수
     */
    int countSearchByTitle(@Param("title") String title);
}