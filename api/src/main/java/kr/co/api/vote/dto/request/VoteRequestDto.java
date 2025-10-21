package kr.co.api.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VoteRequestDto {

    @Schema(description = "투표자 이메일", required = true, example = "user@example.com")
    private String email;
}