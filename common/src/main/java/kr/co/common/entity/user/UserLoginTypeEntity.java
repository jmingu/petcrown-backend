package kr.co.common.entity.user;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_login_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserLoginTypeEntity extends BaseEntity {
    @Id
    private Integer loginTypeId; // 기본 PK

    private String typeName; // EMAIL, GOOGLE, KAKAO 등

}
