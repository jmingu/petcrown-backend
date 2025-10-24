package kr.co.api.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class PetRegistrationRequestDto {

    @Schema(description = "품종 ID", required = false, example = "1")
    private Integer breedId;

    @Schema(description = "기타 품종 (품종 ID가 없을 때)", required = false, example = "푸들30%, 말티즈20%")
    private String customBreed;

    @Schema(description = "이름", required = true, example = "바둑이")
    private String name;

}