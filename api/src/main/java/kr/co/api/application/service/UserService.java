package kr.co.api.application.service;

import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.application.port.out.repository.user.UserRepository;
import kr.co.api.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
