package kr.co.api.adapter.in.web.user;

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

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegistrationRequestDto requestDto) {

        User domain = userDtoConverter.toDomain(requestDto);
        userUseCase.save(domain);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
