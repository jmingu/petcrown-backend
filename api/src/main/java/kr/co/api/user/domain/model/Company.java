package kr.co.api.user.domain.model;

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

    /**
     * CompanyEntity로부터 Company 도메인 생성
     */
    public static Company ofId(Integer companyId) {
        if (companyId == null) {
            return null;
        }
        return new Company(
                companyId,
                null,
                null,
                null,
                null
        );
    }

}
