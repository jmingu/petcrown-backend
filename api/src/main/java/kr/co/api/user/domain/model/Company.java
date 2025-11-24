package kr.co.api.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Company{

    private final Integer companyId;  // 조직 ID

    private final String companyName;  // 조직 이름

    private final String companyCode;  // 조직 고유 코드

    private final String address;  // 조직 주소

    private final String phoneNumber;  // 조직 연락처
    private final String isDefault;


    public static Company ofId(Integer companyId) {
        if (companyId == null) {
            return null;
        }
        return new Company(
                companyId,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static Company of(Integer companyId, String companyName, String companyCode, String address, String phoneNumber,  String isDefault) {
        if (companyId == null) {
            return null;
        }
        return new Company(
                companyId,
                companyName,
                companyCode,
                address,
                phoneNumber,
                isDefault
        );
    }

}
