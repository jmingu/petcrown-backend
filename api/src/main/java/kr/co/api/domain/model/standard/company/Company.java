package kr.co.api.domain.model.standard.company;

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
public class Company{

    private Integer companyId;  // 조직 ID

    private String companyName;  // 조직 이름

    private String companyCode;  // 조직 고유 코드

    private String address;  // 조직 주소

    private String phoneNumber;  // 조직 연락처

}
