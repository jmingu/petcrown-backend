package kr.co.api.game.service;

import kr.co.api.common.repository.CommonDateRepository;
import kr.co.api.game.domain.model.GameScore;
import kr.co.api.game.dto.command.GameScoreDto;
import kr.co.api.game.dto.command.GameScoreRegistrationDto;
import kr.co.api.game.dto.command.WeeklyRankingDto;
import kr.co.api.game.repository.GameScoreRepository;
import kr.co.api.pet.domain.model.Pet;
import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.api.pet.repository.PetRepository;
import kr.co.api.user.domain.vo.Nickname;
import kr.co.api.user.dto.command.UserInfoDto;
import kr.co.api.user.repository.UserRepository;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GameScoreService {

    private final GameScoreRepository gameScoreRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final CommonDateRepository commonDateRepository;

    /**
     * 게임 스코어 등록 또는 업데이트
     * - 같은 주에 같은 userId가 있으면 업데이트
     * - 없으면 새로 등록
     * - 주 조회는 DB의 date_trunc 함수로 처리
     */
    @Transactional
    public void registerOrUpdateScore(GameScoreRegistrationDto registrationDto) {

        // 1. 내부용 Dto → Domain 변환 (정적 팩토리 메서드 사용)
        GameScore newGameScore = GameScore.create(
                registrationDto.getUserId(),
                registrationDto.getScore(),
                registrationDto.getPetId()
        );

        // 나의 펫 조회 후 일치하는게 없으면 오류발생
        List<PetInfoDto> petInfoDtos = petRepository.selectPetListByUserId(registrationDto.getUserId());

        boolean isPetOwned = petInfoDtos.stream()
                .anyMatch(pet -> pet.getPetId().equals(registrationDto.getPetId()));

        if (!isPetOwned) {
            throw new PetCrownException(kr.co.common.enums.BusinessCode.PET_NOT_OWNED);
        }


        // 2. 현재 주의 기존 스코어 조회 (DB의 date_trunc로 주 계산)
        Optional<GameScoreDto> existingScoreOpt = gameScoreRepository.findMaxScoreByUserIdAndCurrentWeek(
                registrationDto.getUserId()
        );

        // 3. 기존 스코어가 있으면 업데이트, 없으면 새로 등록
        if (existingScoreOpt.isPresent()) {
            GameScoreDto existingScore = existingScoreOpt.get();

            // 업데이트용 Domain 생성
            GameScore scoreToUpdate = GameScore.forUpdate(
                    existingScore.getScoreId(),
                    existingScore.getUserId(),
                    existingScore.getWeekStartDate(),
                    registrationDto.getScore(),
                    registrationDto.getPetId()
            );

            gameScoreRepository.update(scoreToUpdate);
            log.info("Game score updated: userId={}, score={}",
                    registrationDto.getUserId(), registrationDto.getScore());
        } else {
            gameScoreRepository.save(newGameScore);
            log.info("Game score registered: userId={}, score={}",
                    registrationDto.getUserId(), registrationDto.getScore());
        }
    }

    /**
     * 이번주 랭킹조회
     */
    public List<WeeklyRankingDto> getWeeklyTopRankings() {
        LocalDate currentWeekStartDate = commonDateRepository.selectCurrentWeekStartDate();
        return gameScoreRepository.findTopRankingsByCurrentWeek(currentWeekStartDate);
    }

    /**
     * 지난주 랭킹조회
     */
    public List<WeeklyRankingDto> getLastWeeklyTopRankings() {
        LocalDate lastWeekStartDate = commonDateRepository.selectLastWeekStartDate();
        return gameScoreRepository.findTopRankingsByCurrentWeek(lastWeekStartDate);
    }

    /**
     * 이번주 내 랭킹조회
     */
    public WeeklyRankingDto getWeeklyMyRankings(Long  userId) {
        LocalDate currentWeekStartDate = commonDateRepository.selectCurrentWeekStartDate();
        return gameScoreRepository.findMyRankingsByCurrentWeek(currentWeekStartDate, userId);
    }

    /**
     * 내 주간 최대 점수 조회 (DB의 date_trunc로 주 계산)
     */
    public GameScoreDto getMyWeeklyScore(Long userId) {
        return gameScoreRepository.findMaxScoreByUserIdAndCurrentWeek(userId)
                .orElse(null);
    }

    /**
     * 닉네임으로 게임 스코어 조회
     */
    public GameScoreDto getWeeklyScoreByNickname(String nicknameValue) {

        Nickname nickname =  Nickname.of(nicknameValue);

        UserInfoDto userInfoDto = userRepository.selectByNickname(nickname.getValue());

        if (userInfoDto == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);

        }
        return gameScoreRepository.findMaxScoreByUserIdAndCurrentWeek(userInfoDto.getUserId())
                .orElse(null);
    }

    /**
     * 게임 점수 삭제
     */
    public void deleteScore(Long userId, Long petId) {
        LocalDate currentWeekStartDate = commonDateRepository.selectCurrentWeekStartDate();

        gameScoreRepository.deleteScoreByUserIdAndCurrentWeek(userId,petId,currentWeekStartDate);

    }


}
