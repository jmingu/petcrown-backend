package kr.co.common.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class EmailGuestEntity {

    private Long emailGuestId; // 기본 PK
    private String email;
    private LocalDate joinDate;
    private String encrypteToken;
    private LocalDateTime createDate;

    // 이메일 게스트 생성자 (joinDate, createDate는 Repository에서 DB의 current_date/current_timestamp 사용)
    public EmailGuestEntity(String email, String encrypteToken) {
        this.email = email;
        this.encrypteToken = encrypteToken;
        // joinDate, createDate는 Repository에서 설정
    }
}