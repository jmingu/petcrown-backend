package kr.co.api.adapter.out.persistence.repository.user;

import kr.co.api.adapter.out.persistence.repository.JpaBaseRepository;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;

public interface JpaUserRepository extends JpaBaseRepository<UserEntity, Long> {
}
