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
public class UserInfoChangeRequestDto {


    @Schema(description = "이름", required = true, example = "홍길동")
    private String name;

    @Schema(description = "닉네임", required = true, example = "닉네임")
    private String nickname;

    @Schema(description = "핸드폰 번호", required = true, example = "01012345678")
    private String phoneNumber;

    // 글자수 체크하기 위해 String로 받는다
    @Schema(description = "생년월일", required = true, example = "19990101")
    private String birthDate;

    @Schema(description = "성별", required = true, example = "M/F")
    private String gender;
}
