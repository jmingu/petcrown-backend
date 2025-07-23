package kr.co.api.application.service.pet;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.api.application.port.in.pet.PetUseCase;
import kr.co.api.application.port.out.repository.pet.PetRepositoryPort;
import kr.co.api.application.port.out.repository.vote.VoteRepositoryPort;
import kr.co.api.application.service.common.FileService;
import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.vote.Vote;
import kr.co.common.enums.FilePathEnum;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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


    /**
     * 펫 등록
     */
    @Transactional
    @Override
    public void savePet(Pet pet, MultipartFile image) {

        List<String> imagePathList = fileService.uploadImageList(FilePathEnum.MY_PET_IMAGE.getFilePath(), List.of(image));
        Pet petAllFiled = Pet.getPetAllFiled(pet.getPetId(), pet.getBreed(), pet.getCustomBreed(), pet.getOwnership(), pet.getUser(),
                pet.getName(), pet.getBirthDate(), pet.getGender(), pet.getWeight(), pet.getHeight(), pet.getIsNeutered(), imagePathList.get(0), pet.getMicrochipId(), pet.getDescription()
        );

        petRepositoryPort.savePet(petAllFiled);
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
        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        return null;
    }


    /**
     * 펫 정보 변경
     */
    @Transactional
    @Override
    public void changeMyPet(Long petId, Pet changePet, Long userId) {

        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 내 소유 펫인지 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 변경된 정보로 펫 업데이트
        petRepositoryPort.updateMyPet(changePet, petId, userId);


    }

    /**
     * 나의 펫 삭제
     */
    @Transactional
    @Override
    public void removeMyPet(Long petId, Long userId) {
        // 펫이 존재하는지 확인
        Pet pet = petRepositoryPort.findPetById(petId);
        if (pet == null) {
            throw new PetCrownException(PET_NOT_FOUND);
        }

        // 내 소유 펫인지 확인
        if (!pet.getUserId().equals(userId)) {
            throw new PetCrownException(PET_NOT_OWNED);
        }

        // 현재 날짜에 해당하는 달의 1일
        LocalDate voteMonth = LocalDate.now().withDayOfMonth(1);
        // 이번달 투표가 이미 등록되었는지 확인
        Vote voteByPetIdAndVoteMonth = voteRepositoryPort.findVoteByPetIdAndVoteMonth(petId, voteMonth);
        if (voteByPetIdAndVoteMonth != null) {
            // 이미 등록된 투표가 있다면 예외 처리
            throw new PetCrownException(VOTE_CANNOT_DELETE);
        }


        // 펫 삭제
        petRepositoryPort.deleteMyPet(petId, userId);
    }



}
