package kr.co.api.domain.port.out;

import kr.co.api.domain.model.user.User;

/**
 * 사용자 저장 출력 포트 (Secondary Port)
 * 사용자 데이터 쓰기 관련 인터페이스
 */
public interface SaveUserPort {
    
    User save(User user);
    
    void delete(User user);
    
    void deleteById(Long userId);
    
    void updateUserInfo(User user, Long userId);
}