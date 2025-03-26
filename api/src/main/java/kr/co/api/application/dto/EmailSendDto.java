package kr.co.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EmailSendDto {


    private String email;
    private String title;
    private String content;
    private String verificationCode;
    private LocalDateTime expiresDate;


}
