package kr.co.api.adapter.in.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NicknameCheckRequestDto {

    @Schema(description = "닉네임", required = true, example = "닉네임")
    private String nickname;

}
