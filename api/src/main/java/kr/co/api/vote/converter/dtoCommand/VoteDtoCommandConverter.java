package kr.co.api.vote.converter.dtoCommand;

import kr.co.api.vote.dto.command.VoteRegistrationDto;
import kr.co.api.vote.dto.command.VoteUpdateDto;
import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.command.VoteListDto;
import kr.co.api.vote.dto.request.VoteRegistrationRequestDto;
import kr.co.api.vote.dto.request.VoteUpdateRequestDto;
import kr.co.api.vote.dto.response.VotePetResponseDto;
import kr.co.api.vote.dto.response.VoteListResponseDto;
import kr.co.api.vote.dto.response.VoteRankingResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vote DTO와 Command DTO 간 양방향 변환을 담당하는 Converter
 * Request DTO -> Command DTO 변환과 Command DTO -> Response DTO 변환을 모두 담당
 */
@Component
public class VoteDtoCommandConverter {

    /**
     * VoteRegistrationRequestDto를 VoteRegistrationDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param userId 현재 로그인된 사용자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public VoteRegistrationDto toCommandDto(VoteRegistrationRequestDto request, Long userId) {
        if (request == null) {
            return null;
        }

        return new VoteRegistrationDto(
                userId,
                request.getPetId(),
                request.getProfileImageUrl(),
                request.getPetModeId()
        );
    }

    /**
     * VoteRegistrationRequestDto를 VoteRegistrationDto로 변환 (수정용)
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param userId 현재 로그인된 사용자 ID
     * @param voteId 수정할 Vote ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public VoteRegistrationDto toCommandDto(VoteRegistrationRequestDto request, Long userId, Long voteId) {
        if (request == null) {
            return null;
        }

        return new VoteRegistrationDto(
                userId,
                request.getPetId(),
                request.getProfileImageUrl(),
                request.getPetModeId(),
                voteId
        );
    }

    /**
     * VoteUpdateRequestDto를 VoteUpdateDto로 변환 (수정용)
     *
     * @param request HTTP 요청으로부터 받은 UpdateRequestDto
     * @param userId 현재 로그인된 사용자 ID
     * @param voteId 수정할 Vote ID
     * @return Service Layer에서 사용할 VoteUpdateDto
     */
    public VoteUpdateDto toUpdateCommandDto(VoteUpdateRequestDto request, Long userId, Long voteId) {
        if (request == null) {
            return null;
        }

        return new VoteUpdateDto(
                voteId,
                userId,
                request.getPetId(),
                request.getProfileImageUrl(),
                request.getPetModeId()
        );
    }

    /**
     * VoteInfoDto를 VotePetResponseDto로 변환
     *
     * @param voteInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public VotePetResponseDto toResponseDto(VoteInfoDto voteInfoDto) {
        if (voteInfoDto == null) {
            return null;
        }

        return new VotePetResponseDto(
                voteInfoDto.getVoteId(),
                voteInfoDto.getPetId(),
                voteInfoDto.getName(),
                voteInfoDto.getGender(),
                voteInfoDto.getBirthDate(),
                voteInfoDto.getBreedId(),
                voteInfoDto.getBreedName(),
                voteInfoDto.getSpeciesId(),
                voteInfoDto.getSpeciesName(),
                voteInfoDto.getPetModeId(),
                voteInfoDto.getPetModeName(),
                voteInfoDto.getDailyVoteCount(),
                voteInfoDto.getWeeklyVoteCount(),
                voteInfoDto.getMonthlyVoteCount(),
                voteInfoDto.getVoteMonth(),
                voteInfoDto.getProfileImageUrl(),
                voteInfoDto.getOwnerEmail()
        );
    }

    /**
     * VoteListDto를 VoteListResponseDto로 변환
     *
     * @param voteListDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public VoteListResponseDto toListResponseDto(VoteListDto voteListDto) {
        if (voteListDto == null) {
            return new VoteListResponseDto(List.of(), 0);
        }

        List<VotePetResponseDto> votes = voteListDto.getVotes().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new VoteListResponseDto(votes, voteListDto.getTotalCount());
    }

    /**
     * List<VoteInfoDto>를 VoteRankingResponseDto로 변환
     *
     * @param voteInfoDtoList Service Layer에서 반환된 랭킹 리스트
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public VoteRankingResponseDto toRankingResponseDto(List<VoteInfoDto> voteInfoDtoList) {
        if (voteInfoDtoList == null) {
            return new VoteRankingResponseDto(List.of());
        }

        List<VotePetResponseDto> ranking = voteInfoDtoList.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new VoteRankingResponseDto(ranking);
    }
}