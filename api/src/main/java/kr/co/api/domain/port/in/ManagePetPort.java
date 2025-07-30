package kr.co.api.domain.port.in;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.domain.model.pet.Pet;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 펫 관리 입력 포트 (Primary Port)
 * 펫 관리 관련 Use Case를 정의
 */
public interface ManagePetPort {
    
    void registerPet(Pet pet, MultipartFile image);
    
    List<MyPetResponseDto> getMyPets(Long userId);
    
    MyPetResponseDto getPet(Long petId, Long userId);
    
    void updatePet(Long petId, Pet updatedPet, Long userId);
    
    void deletePet(Long petId, Long userId);
}