package kr.co.common.entity.vote;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import kr.co.common.entity.pet.PetEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vote")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // protected 생성자 추가

@Getter
public class VoteEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId; // 기본 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")  // 외래키로 PetEntity를 참조
    private PetEntity pet;

    private int dailyVoteCount;
    private int weeklyVoteCount;
    private int monthlyVoteCount;
    private LocalDate voteMonth;
    private String profileImageUrl;

    public VoteEntity(Long createUserId, Long updateUserId, String deleteYn, Long voteId, PetEntity pet, int dailyVoteCount, int weeklyVoteCount, int monthlyVoteCount, LocalDate voteMonth, String profileImageUrl) {
        super(createUserId, updateUserId, deleteYn);
        this.voteId = voteId;
        this.pet = pet;
        this.dailyVoteCount = dailyVoteCount;
        this.weeklyVoteCount = weeklyVoteCount;
        this.monthlyVoteCount = monthlyVoteCount;
        this.voteMonth = voteMonth;
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 투표 엔티티를 생성하는 메서드
     */
    public static VoteEntity createVote(PetEntity pet, int dailyVoteCount,int weeklyVoteCount, int monthlyVoteCount, LocalDate voteMonth, String profileImageUrl) {
        return new VoteEntity(pet.getUser().getUserId(),pet.getUser().getUserId(), "N", null, pet, dailyVoteCount, weeklyVoteCount, monthlyVoteCount, voteMonth, profileImageUrl);
    }




}
