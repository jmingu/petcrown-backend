package kr.co.api.adapter.in.web.pet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.adapter.in.dto.pet.PetRegistrationRequestDto;
import kr.co.api.application.port.in.pet.PetUseCase;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.converter.pet.PetConverter;
import kr.co.api.converter.user.UserConverter;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/pet")
@Tag(name = "Pet", description = "펫 관련 API")
public class PetRestController extends BaseController{

    private final PetConverter petConverter;
    private final PetUseCase petUseCase;

    @AuthRequired(authSkip = true)
    @PostMapping("/v1")
    @Operation(summary = "펫 등록", description = "펫 등록")
    public ResponseEntity<CommonResponseDto> checkEmail(@RequestBody PetRegistrationRequestDto requestDto, Principal principal) {

        Pet pet = petConverter.saveRequestDtoToDomain(requestDto, Long.parseLong(principal.getName()));


        petUseCase.savePet(pet);

        return success();
    }

}
