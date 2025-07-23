package kr.co.api.application.port.in.pet;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.domain.model.pet.Pet;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PetUseCase {

    /**
     * 펫 등록
     */
    void savePet(Pet pet, MultipartFile image);

    /**
     * 펫 조회
     */
    List<MyPetResponseDto> getPet(Long userId);

    /**
     * 펫 단일 조회
     */
    MyPetResponseDto getPetById(Long petId, Long userId);

    /**
     * 펫 정보 변경
     */
    void changeMyPet(Long petId, Pet changePet, Long userId);

    /**
     * 나의 펫 삭제
     */
    void removeMyPet(Long petId, Long userId);
}
