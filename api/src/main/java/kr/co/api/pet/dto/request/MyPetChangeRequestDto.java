package kr.co.api.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MyPetChangeRequestDto {

    @Schema(description = "종류", required = false, example = "0")
    private Integer breedId;

    @Schema(description = "종류 기타면 작성", required = false, example = "푸들30%, 말티즈20%")
    private String customBreed;

    @Schema(description = "이름", required = true, example = "바둑이")
    private String name;

    @Schema(description = "생년월일", required = true, example = "2020-01-01")
    private LocalDate birthDate;

    @Schema(description = "성별", required = true, example = "M")
    private String gender;

    @Schema(description = "소개", required = false, example = "말을 잘 듣는다.")
    private String description;

    @Schema(description = "내장칩 번호", required = false, example = "123456")
    private String microchipId;

}