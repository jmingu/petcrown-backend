package kr.co.api.application.service.pet;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.application.port.in.pet.PetUseCase;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.application.port.out.repository.vote.VoteRepositoryPort;
import kr.co.api.application.service.common.FileService;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.vote.Vote;
import kr.co.api.domain.service.PetDomainService;
import kr.co.api.domain.service.VoteDomainService;
import kr.co.common.enums.FilePathEnum;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static kr.co.common.enums.BusinessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PetService implements PetUseCase {

    private final PetRepositoryPort petRepositoryPort;
    private final VoteRepositoryPort voteRepositoryPort;
    private final FileService fileService;
    private final PetDomainService petDomainService;
    private final VoteDomainService voteDomainService;


    /**
     * 펫 등록
     */
    @Transactional
    @Override
    public void savePet(Pet pet, MultipartFile image) {
        
        // 도메인 비지니스 로직 검증
        petDomainService.validatePetRegistration(pet, pet.getUser());
        
        // 이미지 업로드
        List<String> imagePathList = fileService.uploadImageList(FilePathEnum.MY_PET_IMAGE.getFilePath(), List.of(image));
        
        // 이미지 URL을 포함한 Pet 객체 생성
        Pet petWithImage = Pet.getPetAllFiled(
            pet.getPetId(), pet.getBreed(), pet.getCustomBreed(), pet.getOwnership(), pet.getUser(),
            pet.getName(), pet.getBirthDate(), pet.getGender(), pet.getWeight(), pet.getHeight(), 
            pet.getIsNeutered(), imagePathList.get(0), pet.getMicrochipId(), pet.getDescription()
        );
        
        petRepositoryPort.savePet(petWithImage);
        
        log.info("Pet registered successfully: petId={}, userId={}", petWithImage.getPetId(), petWithImage.getUserId());
    }

    /**
     * 나의 펫 조회
     */
    @Override
    public List<MyPetResponseDto> getPet(Long userId) {
        return petRepositoryPort.findAllPetByUserId(userId);
    }

    /**
     * 펫 단일 조회
     */
    @Override
    public MyPetResponseDto getPetById(Long petId, Long userId) {
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }
        
        // 도메인 비지니스 로직: 접근 권한 확인
        User requestUser = User.createUserById(userId);
        if (!petDomainService.canUserModifyPet(pet, requestUser)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }
        
        // TODO: Pet 엔티티를 MyPetResponseDto로 변환하는 로직 추가 필요
        return null;
    }


    /**
     * 펫 정보 변경
     */
    @Transactional
    @Override
    public void changeMyPet(Long petId, Pet changePet, Long userId) {
        
        Pet existingPet = petRepositoryPort.findPetById(petId);
        if (existingPet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }
        
        User requestUser = User.createUserById(userId);
        
        // 도메인 비지니스 로직 검증
        petDomainService.validatePetUpdate(existingPet, changePet, requestUser);
        
        // 업데이트 수행
        petRepositoryPort.updateMyPet(changePet, petId, userId);
        
        log.info("Pet updated successfully: petId={}, userId={}", petId, userId);
    }

    /**
     * 나의 펫 삭제
     */
    @Transactional
    @Override
    public void removeMyPet(Long petId, Long userId) {
        
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }
        
        User requestUser = User.createUserById(userId);
        
        // 도메인 비지니스 로직: 삭제 권한 확인
        if (!petDomainService.canDeletePet(pet, requestUser)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }
        
        // 도메인 비지니스 로직: 투표와 관련된 삭제 제약 확인
        LocalDate currentVoteMonth = voteDomainService.getCurrentVoteMonth();
        Vote existingVote = voteRepositoryPort.findVoteByPetIdAndVoteMonth(petId, currentVoteMonth);
        
        if (existingVote != null && !voteDomainService.canDeletePetWithVote(pet, currentVoteMonth)) {
            throw new PetCrownException(VOTE_CANNOT_DELETE);
        }
        
        petRepositoryPort.deleteMyPet(petId, userId);
        
        log.info("Pet deleted successfully: petId={}, userId={}", petId, userId);
    }



}
