package kr.co.api.adapter.in.web.pet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.adapter.in.dto.pet.PetRegistrationRequestDto;
import kr.co.api.adapter.in.dto.user.request.EmailCheckRequestDto;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/pet")
@Tag(name = "Pet", description = "펫 관련 API")
public class PetRestController extends BaseController{

    @AuthRequired(authSkip = true)
    @PostMapping("/v1")
    @Operation(summary = "펫 등록", description = "펫 등록")
    public ResponseEntity<CommonResponseDto> checkEmail(@RequestBody PetRegistrationRequestDto requestDto) {

//        userUseCase.checkEmailDuplication(requestDto.getEmail());

        return success();
    }

}
