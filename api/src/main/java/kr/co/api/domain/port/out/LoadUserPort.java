package kr.co.api.domain.port.out;

import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.Email;
import kr.co.api.domain.model.user.vo.Nickname;

import java.util.Optional;

/**
 * 사용자 조회 출력 포트 (Secondary Port)
 * 사용자 데이터 읽기 관련 인터페이스
 */
public interface LoadUserPort {
    
    Optional<User> findById(Long userId);
    
    Optional<User> findByEmail(Email email);
    
    Optional<User> findByNickname(Nickname nickname);
    
    boolean existsByEmail(Email email);
    
    boolean existsByNickname(Nickname nickname);
    
    UserInfoResponseDto getUserInfo(Long userId);
}