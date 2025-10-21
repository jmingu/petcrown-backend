package kr.co.api.user.mapper;

import kr.co.common.entity.user.EmailGuestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface EmailGuestMapper {

    /**
     * 오늘 날짜에 인증된 이메일인지 확인
     */
    EmailGuestEntity selectTodayVerifiedEmail(@Param("email") String email, @Param("joinDate") LocalDate joinDate);

    /**
     * 이메일 게스트 저장
     */
    void insertEmailGuest(EmailGuestEntity emailGuestEntity);
}