package kr.co.api.adapter.in.dto.pet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PetRegistrationRequestDto {
    @Schema(description = "종류", required = true, example = "0")
    private Integer breedId;

    @Schema(description = "종류 기타면 작성", required = false, example = "푸들30%, 말티즈20%")
    private String customBreed;

    @Schema(description = "이름", required = true, example = "바둑이")
    private String name;

    // 글자수 체크하기 위해 String로 받는다
    @Schema(description = "생년월일", required = true, example = "19990101")
    private String birthDate;

    @Schema(description = "성별", required = true, example = "M")
    private String gender;

    @Schema(description = "프로필 이미지 주소", required = true, example = "https://~")
    private String profileImageUrl;

    @Schema(description = "소개", required = true, example = "말을 잘 듣는다.")
    private String description;

    @Schema(description = "내장칩 번호", required = true, example = "123456")
    private String microchipId;

    @Schema(description = "키우는 상태", required = true, example = "1")
    private Integer ownershipId;

}
