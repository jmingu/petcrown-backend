package kr.co.api.adapter.in.web.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.adapter.in.dto.user.request.*;
import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.api.converter.user.UserDtoDomainConverter;
import kr.co.api.domain.model.user.User;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관련 API")
public class UserRestController extends BaseController {

    private final UserUseCase userUseCase;
    private final UserDtoDomainConverter userConverter;

    @AuthRequired(authSkip = true)
    @GetMapping("/v1/check-email")
    @Operation(summary = "회원가입 이메일 중복 검사", description = "회원가입 이메일 중복 검사")
    public ResponseEntity<CommonResponseDto> findEmail(@RequestParam String email) {

        userUseCase.findEmail(email);

        return success();
    }

    @AuthRequired(authSkip = true)
    @GetMapping("/v1/check-nickname")
    @Operation(summary = "회원가입 닉네임 중복 검사", description = "회원가입 닉네임 중복 검사")
    public ResponseEntity<CommonResponseDto> findNickname(@RequestParam String nickname) {

        userUseCase.findNickname(nickname);

        return success();
    }

    @AuthRequired(authSkip = true)
    @PostMapping("/v1")
    @Operation(summary = "회원가입", description = "회원가입")
    public ResponseEntity<CommonResponseDto> register(@RequestBody UserEmailRegistrationRequestDto requestDto) {
        log.debug("UserEmailRegistrationRequestDto ==> {}", requestDto);
        User domain = userConverter.registerRequestDtoToDomain(requestDto);
        userUseCase.saveUser(domain);

        return success();
    }


    @GetMapping("/v1")
    @Operation(summary = "사용자 정보 조회", description = "사용자 정보 조회")
    public ResponseEntity<CommonResponseDto> findUser(Principal principal){

        UserInfoResponseDto user = userUseCase.findUser(Long.parseLong(principal.getName()));
        return success(user);
    }

    @AuthRequired(authSkip = true)
    @PostMapping("/v1/email/verification")
    @Operation(summary = "이메일 인증코드 인증", description = "이메일 인증코드 인증")
    public ResponseEntity<CommonResponseDto> verifyEmailCode(@RequestBody EmailVerificationCodeRequestDto requestDto) {

        userUseCase.checkEmailCode(requestDto.getCode(), requestDto.getEmail());

        return success();
    }

    @AuthRequired(authSkip = true)
    @PostMapping("/v1/email/verification/send")
    @Operation(summary = "인증코드 발송", description = "인증코드 발송")
    public ResponseEntity<CommonResponseDto> sendVerificationCode(@RequestBody EmailVerificationCodeSendRequestDto requestDto) {

        userUseCase.sendVerificationCode(requestDto.getEmail());

        return success();
    }

    @AuthRequired(authSkip = true)
    @PostMapping("/v1/login")
    @Operation(summary = "로그인", description = "쿠키 로그인")
    public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequsetDto requestDto) throws Exception {

        LoginResponseDto responseDto = userUseCase.login(requestDto.getEmail(), requestDto.getPassword());


        return success(responseDto);
    }

    @PutMapping("/v1")
    @Operation(summary = "정보 변경", description = "정보변경")
    public ResponseEntity<CommonResponseDto> changeUserInfo(@RequestBody UserInfoChangeRequestDto requestDto, Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        log.debug("UserEmailRegistrationRequestDto ==> {}", requestDto);
        User domain = userConverter.changeUserRequestDtoToDomain(requestDto, userId);
        userUseCase.changeUserInfo(domain);

        return success();
    }

    @AuthRequired(authSkip = true)
    @PostMapping("/v1/refresh-token")
    @Operation(summary = "리프래쉬 토큰으로 토큰 연장", description = "리프래쉬 토큰으로 토큰 연장")
    public ResponseEntity<CommonResponseDto> refreshToken(@RequestBody RefreshTokenRequsetDto dto) throws Exception {

        LoginResponseDto responseDto = userUseCase.refreshToken(dto.getAccessToken(), dto.getRefreshToken());

        return success(responseDto);
    }



}
