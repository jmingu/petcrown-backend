package kr.co.api.adapter.in.web.user;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.api.adapter.in.dto.user.EmailCheckRequestDto;
import kr.co.api.adapter.in.dto.user.EmailVerificationCodeRequestDto;
import kr.co.api.adapter.in.dto.user.EmailVerificationCodeSendRequestDto;
import kr.co.api.adapter.in.dto.user.UserEmailRegistrationRequestDto;
import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.converter.user.UserDtoConverter;
import kr.co.api.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserRestController {

    private final UserUseCase userUseCase;
    private final UserDtoConverter userDtoConverter;

    @PostMapping("/v1/check-email")
    @Operation(summary = "회원가입 이메일 중복 검사", description = "회원가입 이메일 중복 검사")
    public ResponseEntity<Void> checkEmail(@RequestBody EmailCheckRequestDto requestDto) {

        userUseCase.checkEmailDuplication(requestDto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/v1/register")
    @Operation(summary = "회원가입", description = "회원가입")
    public ResponseEntity<Void> register(@RequestBody UserEmailRegistrationRequestDto requestDto) {

        User domain = userDtoConverter.registerRequestDtoToDomain(requestDto);
        userUseCase.register(domain);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/v1/email/verification")
    @Operation(summary = "이메일 인증코드 인증", description = "이메일 인증코드 인증")
    public ResponseEntity<Void> verifyEmailCode(@RequestBody EmailVerificationCodeRequestDto requestDto) {

        userUseCase.verifyEmailCode(requestDto.getCode(), requestDto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/v1/email/verification/send")
    @Operation(summary = "인증코드 발송", description = "인증코드 발송")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody EmailVerificationCodeSendRequestDto requestDto) {

        userUseCase.sendVerificationCode(requestDto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
