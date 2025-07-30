package kr.co.api.application.service;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.port.in.ManagePetPort;
import kr.co.api.domain.port.out.LoadPetPort;
import kr.co.api.domain.port.out.LoadUserPort;
import kr.co.api.domain.port.out.LoadVotePort;
import kr.co.api.domain.port.out.SavePetPort;
import kr.co.api.domain.service.PetDomainService;
import kr.co.api.domain.service.VoteDomainService;
import kr.co.common.enums.BusinessCode;
import kr.co.common.enums.FilePathEnum;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 펫 관리 애플리케이션 서비스
 * ManagePetPort 구현체 (입력 포트 구현)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PetManagementService implements ManagePetPort {
    
    private final LoadPetPort loadPetPort;
    private final SavePetPort savePetPort;
    private final LoadUserPort loadUserPort;
    private final LoadVotePort loadVotePort;
    private final PetDomainService petDomainService;
    private final VoteDomainService voteDomainService;
    private final kr.co.api.domain.port.in.ManageFilePort filePort;
    
    @Override
    @Transactional
    public void registerPet(Pet pet, MultipartFile image) {
        User owner = loadUserPort.findById(pet.getUserId())
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        // 도메인 비즈니스 로직 검증
        petDomainService.validatePetRegistration(pet, owner);
        
        // 이미지 업로드
        String imageUrl = filePort.uploadSingleImage(FilePathEnum.MY_PET_IMAGE.getFilePath(), image);
        
        // 이미지 URL을 포함한 Pet 객체 생성
        Pet petWithImage = Pet.getPetAllFiled(
                pet.getPetId(), pet.getBreed(), pet.getCustomBreed(), pet.getOwnership(), pet.getUser(),
                pet.getName(), pet.getBirthDate(), pet.getGender(), pet.getWeight(), pet.getHeight(),
                pet.getIsNeutered(), imageUrl, pet.getMicrochipId(), pet.getDescription()
        );
        
        Pet savedPet = savePetPort.save(petWithImage);
        
        log.info("Pet registered successfully: petId={}, userId={}", savedPet.getPetId(), pet.getUserId());
    }
    
    @Override
    public List<MyPetResponseDto> getMyPets(Long userId) {
        return loadPetPort.findAllPetByUserId(userId);
    }
    
    @Override
    public MyPetResponseDto getPet(Long petId, Long userId) {
        Pet pet = loadPetPort.findById(petId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.PET_NOT_FOUND));
        
        User requestUser = User.createUserById(userId);
        
        // 도메인 비즈니스 로직: 접근 권한 확인
        if (!petDomainService.canUserModifyPet(pet, requestUser)) {
            throw new PetCrownException(BusinessCode.PET_NOT_OWNED);
        }
        
        // TODO: Pet을 MyPetResponseDto로 변환하는 로직 필요
        return new MyPetResponseDto(
                pet.getPetId(),
                pet.getNameValue(),
                pet.getBirthDate().toString(),
                pet.getGenderValue(),
                pet.getWeightValue(),
                pet.getHeightValue(),
                pet.getProfileImageUrl()
        );
    }
    
    @Override
    @Transactional
    public void updatePet(Long petId, Pet updatedPet, Long userId) {
        Pet existingPet = loadPetPort.findById(petId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.PET_NOT_FOUND));
        
        User requestUser = User.createUserById(userId);
        
        // 도메인 비즈니스 로직 검증
        petDomainService.validatePetUpdate(existingPet, updatedPet, requestUser);
        
        savePetPort.updatePet(updatedPet, petId, userId);
        
        log.info("Pet updated successfully: petId={}, userId={}", petId, userId);
    }
    
    @Override
    @Transactional
    public void deletePet(Long petId, Long userId) {
        Pet pet = loadPetPort.findById(petId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.PET_NOT_FOUND));
        
        User requestUser = User.createUserById(userId);
        
        // 도메인 비즈니스 로직: 삭제 권한 확인
        if (!petDomainService.canDeletePet(pet, requestUser)) {
            throw new PetCrownException(BusinessCode.PET_NOT_OWNED);
        }
        
        // 도메인 비즈니스 로직: 투표와 관련된 삭제 제약 확인
        LocalDate currentVoteMonth = voteDomainService.getCurrentVoteMonth();
        if (loadVotePort.existsByPetIdAndVoteMonth(petId, currentVoteMonth)) {
            if (!voteDomainService.canDeletePetWithVote(pet, currentVoteMonth)) {
                throw new PetCrownException(BusinessCode.VOTE_CANNOT_DELETE);
            }
        }
        
        savePetPort.deleteById(petId);
        
        log.info("Pet deleted successfully: petId={}, userId={}", petId, userId);
    }
}