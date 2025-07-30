package kr.co.api.domain.repository;

import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.Email;
import kr.co.api.domain.model.user.vo.Nickname;

import java.util.Optional;

public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(Long userId);
    
    Optional<User> findByEmail(Email email);
    
    Optional<User> findByNickname(Nickname nickname);
    
    boolean existsByEmail(Email email);
    
    boolean existsByNickname(Nickname nickname);
    
    void delete(User user);
    
    void deleteById(Long userId);
}