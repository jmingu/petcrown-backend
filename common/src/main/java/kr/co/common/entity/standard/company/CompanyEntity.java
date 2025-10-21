package kr.co.common.entity.standard.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyEntity {

    private Integer companyId;  // 조직 ID

    private String companyName;  // 조직 이름

    private String companyCode;  // 조직 고유 코드

    private String address;  // 조직 주소

    private String phoneNumber;  // 조직 연락처
    
    private String isDefault; // 기본 회사 여부 (Y/N)

    // BaseEntity 필드들
    private LocalDateTime createDate; // 생성 일자
    private Long createUserId;  // 생성 ID
    private LocalDateTime updateDate; // 업데이트 일자
    private Long updateUserId;  // 수정ID
    private LocalDateTime deleteDate; // 삭제 일자
    private Long deleteUserId;  // 삭제ID

}
