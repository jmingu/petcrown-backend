package kr.co.api.user.converter.domainEntity;

import kr.co.api.user.domain.model.Company;
import kr.co.api.user.domain.model.Role;
import kr.co.api.user.domain.model.User;
import kr.co.api.user.domain.vo.*;
import kr.co.common.entity.standard.company.CompanyEntity;
import kr.co.common.entity.standard.role.RoleEntity;
import kr.co.common.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static kr.co.common.constant.DefaultValues.DEFAULT_DELETE_YN;
import static kr.co.common.constant.DefaultValues.DEFAULT_ID;

@Component
public class UserDomainEntityConverter {

    /**
     * 회원가입용 User 도메인 → UserEntity 변환
     */
    public UserEntity toUserEntityForRegistration(User user, RoleEntity roleEntity, CompanyEntity companyEntity) {
        if (user == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();

        return new UserEntity(
                user.getUserId(),
                user.getEmail().getValue(),
                user.getUserUuid(),
                user.getPassword().getValue(),
                roleEntity.getRoleId(), // roleId
                user.getName().getValue(),
                user.getNickname().getValue(),
                user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null,
                user.getBirthDate(),
                user.getGender() != null ? user.getGender().getValue() : null,
                user.getHeight(),
                user.getWeight(),
                "EMAIL", // loginType
                user.getLoginId(),
                user.getIsEmailVerified(),
                user.getIsPhoneNumberVerified(),
                companyEntity.getCompanyId(), // companyId
                user.getDescription(),
                null, // profileImageUrl - FileInfoEntity에서 관리
                now,
                DEFAULT_ID,
                now,
                DEFAULT_ID,
                null,
                null
        );
    }

    /**
     * UserEntity → User 도메인 변환
     */
    public User toUserDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.getUserAllFiled(
                entity.getUserId(),
                Email.of(entity.getEmail()),
                entity.getUserUuid(),
                UserName.of(entity.getName()),
                Nickname.of(entity.getNickname()),
                Password.of(entity.getPassword()),
                Role.ofId(entity.getRoleId()),
                PhoneNumber.of(entity.getPhoneNumber()),
                entity.getBirthDate(),
                Gender.of(entity.getGender()),
                entity.getLoginType(),
                entity.getLoginId(),
                entity.getIsEmailVerified(),
                entity.getIsPhoneNumberVerified(),
                Company.ofId(entity.getCompanyId()),
                entity.getHeight(),
                entity.getWeight(),
                entity.getDescription()
        );
    }

}