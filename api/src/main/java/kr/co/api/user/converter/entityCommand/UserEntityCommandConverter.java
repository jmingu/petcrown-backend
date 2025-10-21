package kr.co.api.user.converter.entityCommand;

import kr.co.api.user.dto.command.UserInfoDto;
import kr.co.common.entity.user.UserEntity;
import org.springframework.stereotype.Component;

/**
 * UserEntity를 Command DTO로 변환하는 Converter
 * Infrastructure Layer와 Application Service Layer 간의 변환을 담당
 */
@Component
public class UserEntityCommandConverter {

    /**
     * UserEntity를 UserInfoDto로 변환
     * 
     * @param userEntity Infrastructure Layer의 Entity
     * @return Application Service Layer의 CommandDto
     */
    public UserInfoDto toUserInfoDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return new UserInfoDto(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getName(),
                userEntity.getNickname(),
                userEntity.getPhoneNumber(),
                userEntity.getProfileImageUrl(),  // MyBatis 쿼리의 file_info JOIN에서 가져온 값
                userEntity.getBirthDate(),
                userEntity.getGender(),
                userEntity.getIsEmailVerified()
        );
    }
}