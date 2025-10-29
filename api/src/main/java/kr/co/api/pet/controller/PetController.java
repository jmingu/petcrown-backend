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
import kr.co.api.pet.dto.response.*;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/pets")
@Tag(name = "Pet", description = "펫 관련 API")
@Validated
public class PetController extends BaseController {

    private final PetService petService;

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

        // RequestDto → CommandDto 변환 (생성자 직접 호출)
        PetRegistrationDto petRegistrationDto = new PetRegistrationDto(
                userId,
                request.getBreedId(),
                request.getCustomBreed(),
                request.getName()
        );

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

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<PetInfoResponseDto> pets = petInfoDtos.stream()
                .map(dto -> new PetInfoResponseDto(
                        dto.getPetId(),
                        dto.getName(),
                        dto.getBreedId(),
                        dto.getBreedName(),
                        dto.getSpeciesId(),
                        dto.getSpeciesName(),
                        dto.getCustomBreed(),
                        dto.getGender(),
                        dto.getBirthDate(),
                        dto.getDescription(),
                        dto.getMicrochipId(),
                        dto.getOwnershipId(),
                        dto.getOwnershipName(),
                        dto.getImageUrl(),
                        dto.getCreateDate()
                ))
                .collect(Collectors.toList());

        PetListResponseDto responseDto = new PetListResponseDto(pets, pets.size());

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

        // CommandDto → ResponseDto 변환 (생성자 직접 호출)
        PetInfoResponseDto responseDto = new PetInfoResponseDto(
                petInfoDto.getPetId(),
                petInfoDto.getName(),
                petInfoDto.getBreedId(),
                petInfoDto.getBreedName(),
                petInfoDto.getSpeciesId(),
                petInfoDto.getSpeciesName(),
                petInfoDto.getCustomBreed(),
                petInfoDto.getGender(),
                petInfoDto.getBirthDate(),
                petInfoDto.getDescription(),
                petInfoDto.getMicrochipId(),
                petInfoDto.getOwnershipId(),
                petInfoDto.getOwnershipName(),
                petInfoDto.getImageUrl(),
                petInfoDto.getCreateDate()
        );

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

        // RequestDto → CommandDto 변환 (생성자 직접 호출)
        PetUpdateDto petUpdateDto = new PetUpdateDto(
                petId,
                userId,
                request.getBreedId(),
                request.getCustomBreed(),
                request.getName(),
                request.getBirthDate(),
                request.getGender(),
                request.getDescription(),
                request.getMicrochipId()
        );

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

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<SpeciesDto> species = speciesInfoDtos.stream()
                .map(dto -> new SpeciesDto(dto.getSpeciesId(), dto.getName()))
                .collect(Collectors.toList());

        SpeciesListResponseDto response = new SpeciesListResponseDto(species);

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

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<BreedDto> breeds = breedInfoDtos.stream()
                .map(dto -> new BreedDto(dto.getBreedId(), dto.getName()))
                .collect(Collectors.toList());

        BreedListResponseDto response = new BreedListResponseDto(breeds);

        return success(response);
    }

    /**
     * 펫 감정 모드 전체 목록 조회
     */
    @GetMapping("/v1/modes")
    @Operation(summary = "펫 감정 모드 목록 조회", description = "펫 감정 모드 전체 목록 조회 (기쁨, 슬픔 등)")
    public ResponseEntity<CommonResponseDto> getAllPetModes() {

        List<PetModeInfoDto> petModeInfoDtos = petService.getAllPetModes();

        // CommandDto 리스트 → ResponseDto 변환 (생성자 직접 호출)
        List<PetModeDto> petModes = petModeInfoDtos.stream()
                .map(dto -> new PetModeDto(dto.getPetModeId(), dto.getModeName()))
                .collect(Collectors.toList());

        PetModeListResponseDto response = new PetModeListResponseDto(petModes);

        return success(response);
    }
}