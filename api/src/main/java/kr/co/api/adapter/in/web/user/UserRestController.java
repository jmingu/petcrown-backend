package kr.co.api.adapter.in.web.user;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.api.adapter.in.dto.user.EmailVerificationRequestDto;
import kr.co.api.adapter.in.dto.user.UserRegistrationRequestDto;
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

    @PostMapping("/verification-code")
    @Operation(summary = "회원가입용 인증번호 이메일 전송", description = "회원가입용 인증번호 이메일 전송")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody EmailVerificationRequestDto requestDto) {
        log.debug("requestDto ==> {}", requestDto);
        userUseCase.sendVerificationCode(requestDto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입")
    public ResponseEntity<Void> register(@RequestBody UserRegistrationRequestDto requestDto) {
        log.debug("requestDto ==> {}", requestDto);
        User domain = userDtoConverter.registerRequestDtoToDomain(requestDto);
        userUseCase.save(domain);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
