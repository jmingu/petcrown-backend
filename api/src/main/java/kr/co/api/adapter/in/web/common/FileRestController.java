package kr.co.api.adapter.in.web.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.adapter.in.dto.pet.request.PetRegistrationRequestDto;
import kr.co.api.domain.model.pet.Pet;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "File", description = "파일 관련 API")
@Slf4j
public class FileRestController extends BaseController {

    @PostMapping("/v1/image")
    @Operation(summary = "이미지 업로드", description = "이미지 업로드, 기본으로 리스트로 받음")
    public ResponseEntity<CommonResponseDto> uploadImageList(@RequestPart(required = false) List<MultipartFile> imgList, Principal principal) {

        Long userId = Long.parseLong(principal.getName());





        return success();
    }

}
