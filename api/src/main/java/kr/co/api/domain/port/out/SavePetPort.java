package kr.co.api.domain.port.out;

import kr.co.api.domain.model.pet.Pet;

/**
 * 펫 저장 출력 포트 (Secondary Port)
 * 펫 데이터 쓰기 관련 인터페이스
 */
public interface SavePetPort {
    
    Pet save(Pet pet);
    
    void delete(Pet pet);
    
    void deleteById(Long petId);
    
    void updatePet(Pet pet, Long petId, Long userId);
}