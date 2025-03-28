package kr.co.api.application.service.user;

import kr.co.api.application.dto.EmailSendDto;
import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.application.service.async.AsyncService;
import kr.co.api.domain.model.Email;
import kr.co.api.domain.model.user.User;
import kr.co.common.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final AsyncService asyncService;


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
    @Override
    public void register(User user) {

        // 비밀번호 일치 확인
        if (!user.isPasswordMatching()) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }

        // 이메일 인증 정보 생성
        EmailSendDto emailSendDto = emailInfo();

        // 회원가입 및 인증코드 저장
        User saveUser = userRepositoryPort.register(user, new Email(null, user, emailSendDto.getVerificationCode(), emailSendDto.getExpiresDate()));

        // 이메일 전송(비동기)
        asyncService.sendEmailAsync(saveUser.getEmail(), emailSendDto.getTitle(), emailSendDto.getContent());
    }

    // 이메일 인증 정보 생성
    private EmailSendDto emailInfo() {
        // 6자리 인증 코드 생성
        String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
        // 인증시간 10분
        LocalDateTime expiresDate = LocalDateTime.now().plusMinutes(10);
        // 미완성
        String title = "인증메일입니다.";
        String content = "<html>"
                + "<body>"
                + "<h1>ImgForest 인증 코드: " + verificationCode + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"

                + "</html>";
        return EmailSendDto.builder().title(title).content(content).verificationCode(verificationCode).expiresDate(expiresDate).build();
    }

    /**
     * 이메일 인증코드 인증
     */
    @Override
    public void verifyEmailCode(String code, String email) {

        // 인증코드 검증
        if (code == null) {
            throw new IllegalArgumentException("값이 없습니다.");
        }

        Optional<User> user  = userRepositoryPort.findByEmail(email);

        // 사용자 없으면 예외 발생
        if (!(user.isPresent())) {
            throw new IllegalArgumentException("없는 유저입니다.");
        }

        // 이메일 검증
        Optional<Email> findEmail = userRepositoryPort.findEmailByUserId(user.get().getUserId());
        if (!(findEmail.isPresent())) {
            throw new IllegalArgumentException("없는 이메일입니다.");
        }

        // 만료 시간 확인
        if (findEmail.get().isExpired()) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }

        // 인증 코드 확인
        if (!findEmail.get().isVerificationCodeValid(code)) {
            throw new IllegalArgumentException("인증 코드가 잘못되었습니다.");
        }

        // 사용자 인증정보 업데이트
        userRepositoryPort.updateEmailVerificationStatus(user.get().getUserId(), "Y");


    }

    /**
     * 인증코드 발송
     */
    @Override
    public void sendVerificationCode(String email) {

        // 이메일 인증 정보 생성
        EmailSendDto emailSendDto = emailInfo();

        User user = new User(email);
        Email emailObject = new Email(null, user, emailSendDto.getVerificationCode(), emailSendDto.getExpiresDate());
        userRepositoryPort.saveEmailVerification(emailObject);

        // 이메일 전송(비동기)
        asyncService.sendEmailAsync(email, emailSendDto.getTitle(), emailSendDto.getContent());

    }

}
