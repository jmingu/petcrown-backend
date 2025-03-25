package kr.co.common.entity.company;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyEntity extends BaseEntity {

    @Id
    private Integer companyId;  // 조직 ID

    private String companyName;  // 조직 이름

    private String companyCode;  // 조직 고유 코드

    private String address;  // 조직 주소

    private String phoneNumber;  // 조직 연락처

}
