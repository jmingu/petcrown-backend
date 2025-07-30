package kr.co.api.domain.port.out;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.domain.model.pet.Pet;

import java.util.List;
import java.util.Optional;

/**
 * 펫 조회 출력 포트 (Secondary Port)
 * 펫 데이터 읽기 관련 인터페이스
 */
public interface LoadPetPort {
    
    Optional<Pet> findById(Long petId);
    
    List<Pet> findByUserId(Long userId);
    
    List<MyPetResponseDto> findAllPetByUserId(Long userId);
    
    boolean existsById(Long petId);
    
    long countByUserId(Long userId);
}