package kr.co.api.adapter.out.persistence.repository.pet.jpa;

import kr.co.api.application.dto.pet.response.MyPetResponseDto;
import kr.co.common.entity.pet.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface JpaPetRepository extends JpaRepository<PetEntity, Long> {

    /**
     * 펫 조회
     * @param userId
     * @return
     */
    @Query("SELECT new kr.co.api.application.dto.pet.response.MyPetResponseDto(p.petId, p.name, p.gender, p.birthDate, b.breedId, b.name, s.speciesId, s.name, p.profileImageUrl, 0) " +
            "FROM PetEntity p " +
            "JOIN p.breed b " +
            "JOIN b.species s " +
            "WHERE p.user.userId = :userId AND p.deleteYn = 'N'" +
            " ORDER BY p.petId ASC")
    List<MyPetResponseDto> findByUser_UserIdAndDeleteYn(Long userId, String deleteYn);

    /**
     * 펫 정보 변경
     */
    @Modifying
    @Query("UPDATE PetEntity p " +
            "SET p.name = :name, " +
            "p.gender = :gender, " +
            "p.birthDate = :birthDate, " +
            "p.breed.breedId = :breedId, " +
            "p.updatedDate = CURRENT_TIMESTAMP, " +
            "p.updateUserId = :userId " +
            "WHERE p.petId = :petId"
    )
    void updateMyPet(String name , String gender, LocalDate birthDate, Integer breedId, Long userId, Long petId);

    /**
     * 나의 펫 삭제
     */
    @Modifying
    @Query("UPDATE PetEntity p " +
            "SET p.deleteYn = 'Y', " +
            "p.updatedDate = CURRENT_TIMESTAMP, " +
            "p.updateUserId = :userId " +
            "WHERE p.petId = :petId"
    )
    void deleteMyPet(Long petId, Long userId);
}
