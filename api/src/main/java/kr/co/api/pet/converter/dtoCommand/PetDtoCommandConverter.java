package kr.co.api.pet.converter.dtoCommand;

import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import kr.co.api.pet.dto.command.SpeciesInfoDto;
import kr.co.api.pet.dto.command.BreedInfoDto;
import kr.co.api.pet.dto.command.PetModeInfoDto;
import kr.co.api.pet.dto.request.MyPetChangeRequestDto;
import kr.co.api.pet.dto.request.PetRegistrationRequestDto;
import kr.co.api.pet.dto.response.PetInfoResponseDto;
import kr.co.api.pet.dto.response.PetListResponseDto;
import kr.co.api.pet.dto.response.SpeciesDto;
import kr.co.api.pet.dto.response.SpeciesListResponseDto;
import kr.co.api.pet.dto.response.BreedDto;
import kr.co.api.pet.dto.response.BreedListResponseDto;
import kr.co.api.pet.dto.response.PetModeDto;
import kr.co.api.pet.dto.response.PetModeListResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pet DTO와 Command DTO 간 양방향 변환을 담당하는 Converter
 * Request DTO -> Command DTO 변환과 Command DTO -> Response DTO 변환을 모두 담당
 */
@Component
public class PetDtoCommandConverter {

    /**
     * PetRegistrationRequestDto를 PetRegistrationDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param userId 현재 로그인된 사용자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public PetRegistrationDto toCommandDto(PetRegistrationRequestDto request, Long userId) {
        if (request == null) {
            return null;
        }

        return new PetRegistrationDto(
                userId,
                request.getBreedId(),
                request.getCustomBreed(),
                request.getName(),
                request.getBirthDate(),
                request.getGender(),
                request.getDescription(),
                request.getMicrochipId()
        );
    }

    /**
     * MyPetChangeRequestDto를 PetUpdateDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param petId 수정할 Pet ID
     * @param userId 현재 로그인된 사용자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public PetUpdateDto toCommandDto(MyPetChangeRequestDto request, Long petId, Long userId) {
        if (request == null) {
            return null;
        }

        return new PetUpdateDto(
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
    }

    /**
     * PetInfoDto를 PetInfoResponseDto로 변환
     *
     * @param petInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public PetInfoResponseDto toResponseDto(PetInfoDto petInfoDto) {
        if (petInfoDto == null) {
            return null;
        }

        return new PetInfoResponseDto(
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
    }

    /**
     * PetInfoDto 리스트를 PetListResponseDto로 변환
     *
     * @param petInfoDtos Service Layer에서 반환된 CommandDto 리스트
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public PetListResponseDto toListResponseDto(List<PetInfoDto> petInfoDtos) {
        if (petInfoDtos == null) {
            return new PetListResponseDto(List.of(), 0);
        }

        List<PetInfoResponseDto> pets = petInfoDtos.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PetListResponseDto(pets, pets.size());
    }

    /**
     * SpeciesInfoDto를 SpeciesDto로 변환
     *
     * @param speciesInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public SpeciesDto toSpeciesResponseDto(SpeciesInfoDto speciesInfoDto) {
        if (speciesInfoDto == null) {
            return null;
        }
        return new SpeciesDto(
                speciesInfoDto.getSpeciesId(),
                speciesInfoDto.getName()
        );
    }

    /**
     * BreedInfoDto를 BreedDto로 변환
     *
     * @param breedInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public BreedDto toBreedResponseDto(BreedInfoDto breedInfoDto) {
        if (breedInfoDto == null) {
            return null;
        }
        return new BreedDto(
                breedInfoDto.getBreedId(),
                breedInfoDto.getName()
        );
    }

    /**
     * List<SpeciesInfoDto>를 SpeciesListResponseDto로 변환
     *
     * @param speciesInfoDtos Service Layer에서 반환된 CommandDto 리스트
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public SpeciesListResponseDto toSpeciesListResponseDto(List<SpeciesInfoDto> speciesInfoDtos) {
        if (speciesInfoDtos == null) {
            return new SpeciesListResponseDto(List.of());
        }
        List<SpeciesDto> species = speciesInfoDtos.stream()
                .map(this::toSpeciesResponseDto)
                .collect(Collectors.toList());
        return new SpeciesListResponseDto(species);
    }

    /**
     * List<BreedInfoDto>를 BreedListResponseDto로 변환
     *
     * @param breedInfoDtos Service Layer에서 반환된 CommandDto 리스트
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public BreedListResponseDto toBreedListResponseDto(List<BreedInfoDto> breedInfoDtos) {
        if (breedInfoDtos == null) {
            return new BreedListResponseDto(List.of());
        }
        List<BreedDto> breeds = breedInfoDtos.stream()
                .map(this::toBreedResponseDto)
                .collect(Collectors.toList());
        return new BreedListResponseDto(breeds);
    }

    /**
     * PetModeInfoDto를 PetModeDto로 변환
     *
     * @param petModeInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public PetModeDto toPetModeResponseDto(PetModeInfoDto petModeInfoDto) {
        if (petModeInfoDto == null) {
            return null;
        }
        return new PetModeDto(
                petModeInfoDto.getPetModeId(),
                petModeInfoDto.getModeName()
        );
    }

    /**
     * List<PetModeInfoDto>를 PetModeListResponseDto로 변환
     *
     * @param petModeInfoDtos Service Layer에서 반환된 CommandDto 리스트
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public PetModeListResponseDto toPetModeListResponseDto(List<PetModeInfoDto> petModeInfoDtos) {
        if (petModeInfoDtos == null) {
            return new PetModeListResponseDto(List.of());
        }
        List<PetModeDto> petModes = petModeInfoDtos.stream()
                .map(this::toPetModeResponseDto)
                .collect(Collectors.toList());
        return new PetModeListResponseDto(petModes);
    }
}