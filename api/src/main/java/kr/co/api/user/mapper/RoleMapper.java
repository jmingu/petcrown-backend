package kr.co.api.user.mapper;

import kr.co.common.entity.standard.role.RoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RoleMapper {
    
    /**
     * 기본 역할 조회 (is_default = 'Y')
     */
    Optional<RoleEntity> selectDefaultRole();
}