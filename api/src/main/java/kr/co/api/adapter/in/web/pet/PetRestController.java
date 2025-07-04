package kr.co.api.adapter.in.web.pet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.adapter.in.dto.pet.request.MyPetChangeRequestDto;
import kr.co.api.adapter.in.dto.pet.request.PetRegistrationRequestDto;
import kr.co.api.adapter.in.dto.user.request.UserInfoChangeRequestDto;
import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.application.port.in.pet.PetUseCase;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.converter.pet.PetDtoDomainConverter;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/pet")
@Tag(name = "Pet", description = "펫 관련 API")
public class PetRestController extends BaseController{


    private final PetUseCase petUseCase;
    private final PetDtoDomainConverter petDtoDomainConverter;

    @PostMapping("/v1")
    @Operation(summary = "나의 펫 등록", description = "나의 펫 등록")
    public ResponseEntity<CommonResponseDto> setPet(@RequestBody PetRegistrationRequestDto dto, Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        Pet pet = petDtoDomainConverter.registerPetDtoToDomain(dto, userId);

        petUseCase.savePet(pet);

        return success();
    }

    @GetMapping("/v1")
    @Operation(summary = "나의 펫 조회", description = "나의 펫 등록")
    public ResponseEntity<CommonResponseDto> getPet(Principal principal) {


        log.debug("Principal ==> {}", principal);
        List<MyPetResponseDto> dtos = petUseCase.getPet(Long.parseLong(principal.getName()));

        return success(dtos);
    }

    @PutMapping("/v1/{petId}")
    @Operation(summary = "나의 펫 정보 변경", description = "나의 펫 정보 변경")
    public ResponseEntity<CommonResponseDto> changeMyPet(@PathVariable Long petId, @RequestBody MyPetChangeRequestDto requestDto, Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        log.debug("UserEmailRegistrationRequestDto ==> {}", requestDto);
        Pet pet = petDtoDomainConverter.changePetDtoToDomain(requestDto, userId);

        petUseCase.changeMyPet(petId, pet, userId);

        return success();
    }

    @DeleteMapping("/v1/{petId}")
    @Operation(summary = "나의 펫 삭제", description = "나의 펫 삭제")
    public ResponseEntity<CommonResponseDto> deleteMyPet(@PathVariable Long petId, Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        petUseCase.removeMyPet(petId, userId);

        return success();
    }

}
