package kr.co.api.user.mapper;

import kr.co.common.entity.standard.company.CompanyEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface CompanyMapper {
    
    /**
     * 기본 회사 조회 (is_default = 'Y')
     */
    Optional<CompanyEntity> selectDefaultCompany();
}