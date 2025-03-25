package kr.co.api.application.service.user;

import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.application.port.out.email.EmailSenderPort;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final EmailSenderPort emailSenderPort;

    /**
     * 이메일 중복검사
     */
    @Override
    public void checkEmailDuplication(String email) {

        // 중복 이메일 검증
        Optional<User> user  = userRepositoryPort.findByEmail(email);

        // 이메일이 이미 존재하면 예외 발생
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

    }
    /**
     * 회원가입
     */
    @Transactional
    @Override
    public void save(User user) {

        // 비밀번호 일치 확인
        if (!user.isPasswordMatching()) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }

        userRepositoryPort.save(user);

        // 미완선
        String content = "<html>"
                + "<body>"
                + "<h1>ImgForest 인증 코드: " + "1252" + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"
                + "</html>";

        // 이메일 전송(비동기로 변경필요)
//        emailSenderPort.sendEmail(user.getEmail(), "test", content);
    }
}
