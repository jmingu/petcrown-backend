package kr.co.api.converter.user;

import kr.co.api.adapter.out.persistence.repository.standard.company.jpa.JpaCompanyRepository;
import kr.co.api.adapter.out.persistence.repository.standard.logintype.jpa.JpaLoginTypeRepository;
import kr.co.api.adapter.out.persistence.repository.standard.role.jpa.JpaRoleRepository;
import kr.co.api.converter.standard.company.CompanyEntityConverter;
import kr.co.api.converter.standard.logintype.LoginTypeEntityConverter;
import kr.co.api.converter.standard.role.RoleEntityConverter;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.*;
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
    public UserEntity toEntity(User user) {
        return UserEntity.createUserEntity(
                user.getUserId(),
                user.getEmailValue(),
                user.getUserUuid(),
                user.getPassword(),
                user.getRole() != null ? jpaRoleRepository.getReferenceById(0) : null,
                user.getNameValue(),
                user.getNicknameValue(),
                user.getPhoneNumberValue(),
                user.getProfileImageUrl(),
                user.getBirthDate(),
                user.getGenderValue(),
                null,
                null,
                user.getLoginType() != null ? jpaLoginTypeRepository.getReferenceById(0) : null,
                user.getLoginId(),
                user.getIsEmailVerified(),
                user.getIsPhoneNumberVerified(),
                user.getCompany() != null ? jpaCompanyRepository.getReferenceById(0) : null,
                null
        );
    }
    
    /**
     * User 도메인 객체를 UserEntity로 변환 (기존 호환성)
     */
    public UserEntity registerUserToEntity(User user) {
        return toEntity(user);
    }

    /**
     * UserEntity를 User 도메인 객체로 변환
     */
    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        
        return User.getUserAllFiled(
                userEntity.getUserId(),
                userEntity.getEmail() != null ? new Email(userEntity.getEmail()) : null,
                userEntity.getUserUuid(),
                userEntity.getName() != null ? new UserName(userEntity.getName()) : null,
                userEntity.getNickname() != null ? new Nickname(userEntity.getNickname()) : null,
                userEntity.getPassword(),
                userEntity.getRole() != null ? roleEntityConverter.toDomain(userEntity.getRole()) : null,
                userEntity.getPhoneNumber() != null ? new PhoneNumber(userEntity.getPhoneNumber()) : null,
                userEntity.getProfileImageUrl(),
                userEntity.getBirthDate(),
                userEntity.getGender() != null ? new Gender(userEntity.getGender()) : null,
                userEntity.getLoginType() != null ? loginTypeEntityConverter.toDomain(userEntity.getLoginType()) : null,
                userEntity.getLoginId(),
                userEntity.getIsEmailVerified(),
                userEntity.getIsPhoneNumberVerified(),
                userEntity.getCompany() != null ? companyEntityConverter.toDomain(userEntity.getCompany()) : null
        );
    }
    
    /**
     * entity -> User (기존 호환성)
     */
    public User toDomainBasic(UserEntity userEntity) {
        return toDomain(userEntity);
    }
}
