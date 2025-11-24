package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 이메일 게스트 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class EmailGuestDto {

    private final Long emailGuestId;
    private final String email;
    private final LocalDateTime createDate;

    /**
     * 등록용 생성자 (emailGuestId 없음)
     */
    public EmailGuestDto(String email) {
        this(null, email, null);
    }
}
