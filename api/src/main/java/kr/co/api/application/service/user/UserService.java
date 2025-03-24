package kr.co.api.application.service.user;

import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.application.port.out.email.EmailSenderPort;
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
    private final EmailSenderPort emailSenderPort;

    @Override
    public void sendVerificationCode(String email) {
        String content = "<html>"
                + "<body>"
                + "<h1>ImgForest 인증 코드: " + "1252" + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"
                + "</html>";
        // 중복 이메일 검증

        // 이메일 전송
        emailSenderPort.sendEmail("jmg173@naver.com", "test", content);

    }

    @Override
    public void save(User user) {

        userRepository.save(user);


    }
}
