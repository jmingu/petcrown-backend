package kr.co.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.user.converter.dtoCommand.UserDtoCommandConverter;
import kr.co.api.user.dto.command.*;
import kr.co.api.user.dto.request.*;
import kr.co.api.user.dto.response.LoginResponseDto;
import kr.co.api.user.dto.response.UserInfoResponseDto;
import kr.co.api.user.service.UserService;
import kr.co.api.user.service.VotingEmailVerificationService;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "유저 관련 API")
public class UserRestController extends BaseController {
    
    private final UserService userService;
    private final VotingEmailVerificationService votingEmailVerificationService;
    private final UserDtoCommandConverter userDtoCommandConverter;

    @AuthRequired(authSkip = true)
    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인")
    @GetMapping("/v1/check-email")
    public ResponseEntity<CommonResponseDto> checkEmailDuplicate(@RequestParam String email) {

        userService.checkEmailDuplicate(email);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인")
    @GetMapping("/v1/check-nickname")
    public ResponseEntity<CommonResponseDto> checkNicknameDuplicate(@RequestParam String nickname) {


        userService.checkNicknameDuplicate(nickname);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/v1")
    public ResponseEntity<CommonResponseDto> createUser(@RequestBody UserRegistrationRequestDto request) {

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        UserRegistrationDto userRegistrationDto = userDtoCommandConverter.toCommandDto(request);

        userService.createUser(userRegistrationDto);

        return success();
    }

    @AuthRequired(authSkip = true)
    @Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드 검증")
    @PostMapping("/v1/email/verification")
    public ResponseEntity<CommonResponseDto> verifyEmailCode(@RequestBody EmailVerificationRequestDto request) {

        userService.verifyEmailCode(request.getEmail(), request.getCode());

        return success();
    }
    

    @AuthRequired(authSkip = true)
    @Operation(summary = "이메일 인증 코드 재발송", description = "이메일 인증 코드가 안왔을 경우 재발송")
    @PostMapping("/v1/email/verification/send")
    public ResponseEntity<CommonResponseDto> sendEmailVerificationCode( @RequestBody EmailVerificationSendRequestDto dto) {

        userService.sendEmailVerificationCode(dto.getEmail());

        return success();
    }


    @AuthRequired(authSkip = true)
    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/v1/login")
    public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequestDto request) throws Exception{

        LoginTokenDto login = userService.login(request.getEmail(), request.getPassword());
        LoginResponseDto responseDto = userDtoCommandConverter.toResponseDto(login);

        return success(responseDto);
    }


    @Operation(summary = "사용자 정보 조회", description = "사용자 정보 조회")
    @GetMapping("/v1/info")
    public ResponseEntity<CommonResponseDto> getUserInfo(Principal principal) {

        UserInfoDto userInfoDto = userService.getUserInfo(Long.parseLong(principal.getName()));
        UserInfoResponseDto responseDto = userDtoCommandConverter.toResponseDto(userInfoDto);

        return success(responseDto);
    }
    @AuthRequired(authSkip = true)
    @Operation(summary = "토큰 갱신", description = "토큰 갱신")
    @PostMapping("/v1/refresh-token")
    public ResponseEntity<CommonResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto requestDto) throws Exception {

        LoginTokenDto login = userService.refreshToken(requestDto.getAccessToken(), requestDto.getRefreshToken());
        LoginResponseDto responseDto = userDtoCommandConverter.toResponseDto(login);

        return success(responseDto);
    }



    /**
     * 사용자 정보 수정
     */
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보 수정")
    @PutMapping("/v1/info")
    public ResponseEntity<CommonResponseDto> updateUserInfo(
            Principal principal, @RequestBody UserUpdateRequestDto request) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        UserUpdateDto userUpdateDto = userDtoCommandConverter.toCommandDto(request, userId);

        userService.updateUserInfo(userUpdateDto);

        return success();
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경")
    @PutMapping("/v1/password")
    public ResponseEntity<CommonResponseDto> updatePassword(
            Principal principal, @RequestBody PasswordUpdateRequestDto request) {

        Long userId = Long.parseLong(principal.getName());

        // RequestDto → CommandDto 변환 (Converter 패턴 사용)
        PasswordUpdateDto passwordUpdateDto = userDtoCommandConverter.toCommandDto(request, userId);

        userService.updatePassword(passwordUpdateDto);

        return success();
    }

    /**
     * 투표 가능 인증 이메일 발송
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "투표 가능 인증 이메일 보내기", description = "금일 투표 가능 인증 이메일 보내기")
    @PostMapping("/v1/vote-verification/send")
    public ResponseEntity<CommonResponseDto> sendVotingVerificationEmail(
            @RequestBody VotingEmailVerificationRequestDto request) {

        votingEmailVerificationService.sendVotingVerificationEmail(request.getEmail());

        return success();
    }

    /**
     * 투표 가능 이메일 인증
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "투표 가능 이메일 인증", description = "투표 가능 이메일 인증")
    @PostMapping("/v1/vote-verification")
    public ResponseEntity<CommonResponseDto> confirmVotingEmail(
            @RequestBody VotingEmailConfirmationRequestDto request) {

        votingEmailVerificationService.confirmVotingEmail(request.getEmail(), request.getEncryptedToken());

        return success();
    }


    /**
     * 오늘 인증된 이메일인지 확인
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "오늘 인증된 이메일 확인", description = "해당 이메일이 오늘 인증되었는지 확인 (인증되지 않으면 에러)")
    @GetMapping("/v1/vote-verification/check")
    public ResponseEntity<CommonResponseDto> checkVerifiedEmailToday(@RequestParam String email) {

        votingEmailVerificationService.checkVerifiedEmailToday(email);

        return success();
    }

    /**
     * GET 방식으로 투표 이메일 인증 (이메일 링크 클릭용)
     */
    @AuthRequired(authSkip = true)
    @Operation(summary = "투표 이메일 인증 (링크 클릭)", description = "이메일 링크 클릭으로 투표 이메일 인증")
    @GetMapping("/v1/verify-voting-email")
    public ResponseEntity<String> verifyVotingEmailByLink(
            @RequestParam String email,
            @RequestParam String token) {

        try {
            votingEmailVerificationService.confirmVotingEmail(email, token);
            return ResponseEntity.ok(
                "<html><body style='text-align:center; padding:50px; font-family:Arial;'>" +
                "<h2>🎉 투표 인증 완료!</h2>" +
                "<p>오늘 하루 동안 투표에 참여하실 수 있습니다.</p>" +
                "<p>창을 닫고 투표를 진행해주세요.</p>" +
                "</body></html>"
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                "<html><body style='text-align:center; padding:50px; font-family:Arial;'>" +
                "<h2>❌ 인증 실패</h2>" +
                "<p>인증에 실패했습니다: " + e.getMessage() + "</p>" +
                "<p>다시 시도해주세요.</p>" +
                "</body></html>"
            );
        }
    }
}