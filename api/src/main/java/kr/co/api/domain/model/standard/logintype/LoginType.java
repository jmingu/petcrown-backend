package kr.co.api.domain.model.standard.logintype;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginType {
    private Integer loginTypeId;

    private String typeName; // EMAIL, GOOGLE, KAKAO 등

}
