package kr.co.api.pet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.pet.dto.request.MyPetChangeRequestDto;
import kr.co.api.pet.dto.request.PetRegistrationRequestDto;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.api.pet.dto.command.SpeciesInfoDto;
import kr.co.api.pet.dto.command.BreedInfoDto;
import kr.co.api.pet.dto.command.PetModeInfoDto;
import kr.co.api.pet.dto.response.PetInfoResponseDto;
import kr.co.api.pet.dto.response.PetListResponseDto;
import kr.co.api.pet.dto.response.SpeciesListResponseDto;
import kr.co.api.pet.dto.response.BreedListResponseDto;
import kr.co.api.pet.dto.response.PetModeListResponseDto;
import kr.co.api.pet.converter.dtoCommand.PetDtoCommandConverter;
import kr.co.api.pet.service.PetService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/pets")
@Tag(name = "Pet", description = "펫 관련 API")
@Validated
public class PetController extends BaseController {

    private final PetService petService;
    private final PetDtoCommandConverter petDtoCommandConverter;

    /**
     * 나의 펫 등록
     */
    @PostMapping(value = "/v1",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "나의 펫 등록", description = "나의 펫 등록")
    public ResponseEntity<CommonResponseDto> createPet(
            @RequestPart("image") MultipartFile image,
            @RequestPart("data") PetRegistrationRequestDto request,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        PetRegistrationDto petRegistrationDto = petDtoCommandConverter.toCommandDto(request, userId);

        petService.insertPet(petRegistrationDto, image);

        return success();
    }
    /**
     * 나의 펫 목록 조회
     */
    @GetMapping("/v1")
    @Operation(summary = "나의 펫 목록 조회", description = "나의 펫 목록 조회")
    public ResponseEntity<CommonResponseDto> getPetList(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        List<PetInfoDto> petInfoDtos = petService.selectPetList(userId);
        PetListResponseDto responseDto = petDtoCommandConverter.toListResponseDto(petInfoDtos);

        return success(responseDto);
    }

    /**
     * 펫 상세 조회
     */
    @GetMapping("/v1/{petId}")
    @Operation(summary = "펫 상세 조회", description = "펫 상세 정보 조회")
    public ResponseEntity<CommonResponseDto> getPet(@PathVariable Long petId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        PetInfoDto petInfoDto = petService.selectPet(petId, userId);
        PetInfoResponseDto responseDto = petDtoCommandConverter.toResponseDto(petInfoDto);

        return success(responseDto);
    }
    /**
     * 나의 펫 정보 수정
     */
    @PutMapping(value = "/v1/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "나의 펫 정보 수정", description = "나의 펫 정보 수정")
    public ResponseEntity<CommonResponseDto> updatePet(
            @PathVariable Long petId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart("data") MyPetChangeRequestDto request,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        PetUpdateDto petUpdateDto = petDtoCommandConverter.toCommandDto(request, petId, userId);

        petService.updatePet(petUpdateDto, image);

        return success();
    }

    /**
     * 나의 펫 삭제
     */
    @DeleteMapping("/v1/{petId}")
    @Operation(summary = "나의 펫 삭제", description = "나의 펫 삭제")
    public ResponseEntity<CommonResponseDto> deletePet(
            @PathVariable Long petId,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        petService.deletePet(petId, userId);

        return success();
    }

    /**
     * 종 목록 조회 (강아지, 고양이)
     */
    @GetMapping("/v1/species")
    @Operation(summary = "종 목록 조회", description = "종 목록 조회 (species_id != 0)")
    public ResponseEntity<CommonResponseDto> getAllSpecies() {

        List<SpeciesInfoDto> speciesInfoDtos = petService.getAllSpecies();
        SpeciesListResponseDto response = petDtoCommandConverter.toSpeciesListResponseDto(speciesInfoDtos);

        return success(response);
    }

    /**
     * 품종 목록 조회 (특정 종)
     */
    @GetMapping("/v1/breeds")
    @Operation(summary = "품종 목록 조회", description = "특정 종의 품종 목록 조회")
    public ResponseEntity<CommonResponseDto> getBreedsBySpeciesId(
            @RequestParam Long speciesId) {

        List<BreedInfoDto> breedInfoDtos = petService.getBreedsBySpeciesId(speciesId);
        BreedListResponseDto response = petDtoCommandConverter.toBreedListResponseDto(breedInfoDtos);

        return success(response);
    }

    /**
     * 펫 감정 모드 전체 목록 조회
     */
    @GetMapping("/v1/modes")
    @Operation(summary = "펫 감정 모드 목록 조회", description = "펫 감정 모드 전체 목록 조회 (기쁨, 슬픔 등)")
    public ResponseEntity<CommonResponseDto> getAllPetModes() {

        List<PetModeInfoDto> petModeInfoDtos = petService.getAllPetModes();
        PetModeListResponseDto response = petDtoCommandConverter.toPetModeListResponseDto(petModeInfoDtos);

        return success(response);
    }
}