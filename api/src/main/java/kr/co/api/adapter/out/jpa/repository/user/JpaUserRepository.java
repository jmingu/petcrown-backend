package kr.co.api.adapter.out.jpa.repository.user;

import kr.co.api.adapter.out.jpa.repository.JpaBaseRepository;
import kr.co.common.entity.user.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaBaseRepository<UserEntity, Long> {
}
