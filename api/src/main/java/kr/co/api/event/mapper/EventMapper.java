package kr.co.api.event.mapper;

import kr.co.api.event.dto.command.EventUpdateDto;
import kr.co.common.entity.event.EventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {

    /**
     * 이벤트 저장
     */
    void insertEvent(EventEntity event);

    /**
     * 이벤트 ID로 조회
     */
    EventEntity selectByEventId(@Param("eventId") Long eventId);

    /**
     * 활성화된 이벤트 목록 조회 (페이징)
     */
    List<EventEntity> selectActiveEvents(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 전체 이벤트 목록 조회 (관리자용)
     */
    List<EventEntity> selectAllEvents(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 활성화된 이벤트 개수 조회
     */
    int countActiveEvents();

    /**
     * 전체 이벤트 개수 조회
     */
    int countAllEvents();

    /**
     * 이벤트 수정
     */
    void updateEvent(EventUpdateDto eventUpdateDto);

    /**
     * 조회수 증가
     */
    void incrementViewCount(@Param("eventId") Long eventId);

    /**
     * 이벤트 삭제 (논리 삭제)
     */
    void deleteById(@Param("eventId") Long eventId, @Param("deleteUserId") Long deleteUserId);

    /**
     * 제목으로 이벤트 검색
     */
    List<EventEntity> searchByTitle(@Param("title") String title, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 제목으로 검색된 이벤트 개수
     */
    int countSearchByTitle(@Param("title") String title);
}
