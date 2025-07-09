package kr.co.api.converter.user;

import kr.co.api.adapter.out.persistence.repository.standard.company.jpa.JpaCompanyRepository;
import kr.co.api.adapter.out.persistence.repository.standard.logintype.jpa.JpaLoginTypeRepository;
import kr.co.api.adapter.out.persistence.repository.standard.role.jpa.JpaRoleRepository;
import kr.co.api.converter.standard.company.CompanyEntityConverter;
import kr.co.api.converter.standard.logintype.LoginTypeEntityConverter;
import kr.co.api.converter.standard.role.RoleEntityConverter;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDomainEntityConverter {

    private final RoleEntityConverter roleEntityConverter;
    private final LoginTypeEntityConverter loginTypeEntityConverter;
    private final CompanyEntityConverter companyEntityConverter;

    private final JpaRoleRepository jpaRoleRepository;
    private final JpaLoginTypeRepository jpaLoginTypeRepository;
    private final JpaCompanyRepository jpaCompanyRepository;


    /**
     * User 도메인 객체를 UserEntity로 변환
     */
    public UserEntity registerUserToEntity(User user) {

        return UserEntity.createUserEntity(
                user.getUserId(),
                user.getEmail(),
                user.getUserUuid(),
                user.getPassword(),
                jpaRoleRepository.getReferenceById(0),
                user.getName(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getBirthDate(),
                user.getGender(),
                null,
                null,
                jpaLoginTypeRepository.getReferenceById(0),
                user.getLoginId(),
                user.getIsEmailVerified(),
                user.getIsPhoneNumberVerified(),
                jpaCompanyRepository.getReferenceById(0),
                null
        );
    }

    /**
     * entity -> User
     * 필요 없는 필드는 제외한 변환 메서드
     */
    public User toDomainBasic(UserEntity userEntity) {
        return User.getUserAllFiled(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getUserUuid(),
                userEntity.getName(),
                userEntity.getNickname(),
                userEntity.getPassword(),
                null,
                userEntity.getPhoneNumber(),
                userEntity.getProfileImageUrl(),
                userEntity.getBirthDate(),
                userEntity.getGender(),
                null,
                userEntity.getLoginId(),
                userEntity.getIsEmailVerified(),
                userEntity.getIsPhoneNumberVerified(),
                null
        );
    }
}
