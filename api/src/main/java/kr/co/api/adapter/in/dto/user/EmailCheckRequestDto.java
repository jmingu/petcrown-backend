package kr.co.api.adapter.in.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmailCheckRequestDto {

    @Schema(description = "이메일", required = true, example = "example@example.com")
    private String email;

}
