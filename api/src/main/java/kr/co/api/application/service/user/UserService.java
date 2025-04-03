package kr.co.api.application.service.user;

import kr.co.common.dto.EmailContentDto;
import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.application.service.async.AsyncService;
import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.util.JwtUtil;
import kr.co.api.domain.model.user.Email;
import kr.co.api.domain.model.user.User;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final AsyncService asyncService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperty jwtProperty;
    private final JwtUtil jwtUtil;


    /**
     * 이메일 중복검사
     */
    @Override
    public void checkEmailDuplication(String email) {

        // 중복 이메일 검증
        User user  = userRepositoryPort.findByEmail(email);

        // 이메일이 이미 존재하면 예외 발생
        if (user != null) {
            throw new PetCrownException("이미 사용 중인 이메일입니다.");
        }

    }
    /**
     * 회원가입
     */
    @Override
    public void saveUser(User user) {

        // 중복 이메일 검증
        User byEmail = userRepositoryPort.findByEmail(user.getEmail());
        // 이메일이 이미 존재하면 예외 발생
        if (byEmail != null) {
            throw new PetCrownException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호와 비밀번호 확인 일치 확인
        if (!user.isPasswordMatching()) {
            throw new PetCrownException("패스워드가 일치하지 않습니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        // 비밀번호 인코딩 후 유저 재 생성
        User encodePasswordUser = user.encodePassword(encodedPassword);

        // 이메일 생성
        Email email = new Email(user);

        // 인증코드 생성
        email.generateVerificationCode();
        EmailContentDto emailContentDto = EmailUtil.generateEmailContent(email.getVerificationCode());


        // 회원가입 및 인증코드 저장
        User registerUser = userRepositoryPort.saveUser(encodePasswordUser, email);

        // 이메일 전송(비동기)
        asyncService.sendEmailAsync(registerUser.getEmail(), emailContentDto.getTitle(), emailContentDto.getContent());
    }

    /**
     * 이메일 인증코드 인증
     */
    @Override
    public void verifyEmailCode(String code, String email) {

        // 인증코드 검증
        if (code == null) {
            throw new PetCrownException("값이 없습니다.");
        }

        // 이메일로 사용자 조회
        User user  = userRepositoryPort.findByEmail(email);

        // 사용자 없으면 예외 발생
        if (user == null) {
            throw new PetCrownException("없는 유저입니다.");
        }

        // 이메일 검증
        Email findEmail = userRepositoryPort.findEmailByUserId(user.getUserId());
        if (findEmail == null) {
            throw new PetCrownException("없는 이메일입니다.");
        }

        // 만료 시간 확인
        if (findEmail.isExpired()) {
            throw new PetCrownException("인증 코드가 만료되었습니다.");
        }

        // 인증 코드 확인
        if (!findEmail.isVerificationCodeValid(code)) {
            throw new PetCrownException("인증 코드가 잘못되었습니다.");
        }

        // 사용자 인증정보 업데이트
        userRepositoryPort.updateEmailVerificationStatus(user.getUserId(), "Y");


    }

    /**
     * 인증코드 발송
     */
    @Override
    public void sendVerificationCode(String email) {
        // 이메일로 사용자 조회
        User user  = userRepositoryPort.findByEmail(email);

        // 사용자 없으면 예외 발생
        if (user == null) {
            throw new PetCrownException("없는 유저입니다.");
        }

        // 이메일 인증 정보 생성
        Email emailObject = new Email(user);
        emailObject.generateVerificationCode();
        EmailContentDto emailContentDto = EmailUtil.generateEmailContent(emailObject.getVerificationCode());

        // 검증 업데이트/등록
        userRepositoryPort.saveEmailVerification(emailObject);

        // 이메일 전송(비동기)
        asyncService.sendEmailAsync(email, emailContentDto.getTitle(), emailContentDto.getContent());

    }

    /**
     * 로그인
     */
    @Override
    public LoginResponseDto login(String email, String password) throws Exception{
        // 사용자 조회
        // 중복 이메일 검증
        User user  = userRepositoryPort.findByEmail(email);

        // 사용자 존재하지 않으면 값이 없으면 예외를 던지고
        if (user == null) {
            new PetCrownException("존재하지 않는 이메일입니다.");
        }

        // 비밀번호 확인
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            new PetCrownException("비빌번호 오류입니다.");
        }

        // jwt 토큰 생성
        // 엑세스토큰발행
        String accessToken = jwtUtil.makeAuthToken(user, jwtProperty.getExpiredTime());
        // 리프레시 토큰발행
        String refreshToken = jwtUtil.makeAuthToken(user, jwtProperty.getExpiredRefreshTime());


        return new LoginResponseDto(accessToken, refreshToken);
    }

    /**
     * 리프래쉬 토큰으로 엑세스 토큰 연장
     */
    @Override
    public LoginResponseDto refreshToken(String token, Long userid) throws Exception {

        User user = new User(userid);
        // jwt 토큰 생성
        // 엑세스토큰발행
        String accessToken = jwtUtil.makeAuthToken(user, jwtProperty.getExpiredTime());
        // 리프레시 토큰발행
        String refreshToken = jwtUtil.makeAuthToken(user, jwtProperty.getExpiredRefreshTime());

        return new LoginResponseDto(accessToken, refreshToken);

    }


}
