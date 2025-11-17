package kr.co.api.user.service;

import kr.co.api.common.service.EmailService;
import kr.co.api.user.repository.EmailGuestRepository;
import kr.co.api.user.repository.UserRepository;
import kr.co.common.dto.EmailContentDto;
import kr.co.common.entity.user.EmailGuestEntity;
import kr.co.common.entity.user.UserEntity;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.CryptoUtil;
import kr.co.common.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static kr.co.common.enums.BusinessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VotingEmailVerificationService {

    private final UserRepository userRepository;
    private final EmailGuestRepository emailGuestRepository;
    private final EmailService emailService;

    @Value("${petcrown.encryption.key}")
    private String encryptionKey;

    @Value("${petcrown.frontend.url}")
    private String frontendUrl;

    /**
     * 투표용 이메일 인증 메일 발송
     */
    @Transactional
    public void sendVotingVerificationEmail(String email) {
        LocalDate today = LocalDate.now();

        // 1. 이미 등록된 회원 이메일인지 확인
        UserEntity existingUser = userRepository.selectByEmail(email);
        if (existingUser != null) {
            throw new PetCrownException(EMAIL_ALREADY_REGISTERED);
        }

        // 2. 오늘 이미 인증된 이메일인지 확인 (DB의 current_date 사용)
        EmailGuestEntity existingEmailGuest = emailGuestRepository.selectTodayVerifiedEmail(email);
        if (existingEmailGuest != null) {
            throw new PetCrownException(EMAIL_ALREADY_VERIFIED_TODAY);
        }

        // 3. 암호화 토큰 생성: 이메일_오늘일자(yyyymmdd)_petcrown
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String plainToken = email + "_" + todayStr + "_petcrown";

        try {
            String encryptedToken = CryptoUtil.encrypt(plainToken, encryptionKey);

            // 4. HTML 이메일 콘텐츠 생성
            EmailContentDto emailContent = EmailUtil.generateVotingEmailContent(email, encryptedToken, frontendUrl);

            // 5. 이메일 발송
            emailService.sendEmail(email, emailContent.getSubject(), emailContent.getBody());

            log.info("Voting verification email sent successfully to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send voting verification email to: {}, error: {}", email, e.getMessage());
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 투표용 이메일 인증 확인
     */
    @Transactional
    public void confirmVotingEmail(String email, String encryptedToken) {
        LocalDate today = LocalDate.now();

        try {
            // 1. 토큰 복호화
            String decryptedToken = CryptoUtil.decrypt(encryptedToken, encryptionKey);

            // 2. 토큰 형식 검증: 이메일_오늘일자(yyyymmdd)_petcrown
            String[] tokenParts = decryptedToken.split("_");
            if (tokenParts.length != 3 || !"petcrown".equals(tokenParts[2])) {
                throw new PetCrownException(INVALID_VERIFICATION_TOKEN);
            }

            String tokenEmail = tokenParts[0];
            String tokenDate = tokenParts[1];
            String todayStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 3. 이메일 일치 확인
            if (!email.equals(tokenEmail)) {
                throw new PetCrownException(INVALID_VERIFICATION_TOKEN);
            }

            // 4. 날짜 일치 확인 (오늘 날짜만 유효)
            if (!todayStr.equals(tokenDate)) {
                throw new PetCrownException(VERIFICATION_TOKEN_EXPIRED);
            }

            // 5. 이미 인증된 이메일인지 재확인 (DB의 current_date 사용)
            EmailGuestEntity existingEmailGuest = emailGuestRepository.selectTodayVerifiedEmail(email);
            if (existingEmailGuest != null) {
                throw new PetCrownException(EMAIL_ALREADY_VERIFIED_TODAY);
            }

            // 6. 이메일 게스트 테이블에 저장 (joinDate, createDate는 Repository에서 DB 기준으로 설정)
            EmailGuestEntity emailGuest = new EmailGuestEntity(email, encryptedToken);
            Long emailGuestId = emailGuestRepository.insertEmailGuest(emailGuest);

            log.info("Voting email verification completed successfully for: {} (emailGuestId={})", email, emailGuestId);

        } catch (PetCrownException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify voting email for: {}, error: {}", email, e.getMessage());
            throw new PetCrownException(INVALID_VERIFICATION_TOKEN);
        }
    }

    /**
     * 오늘 인증된 이메일인지 확인 (예외 발생 버전, DB의 current_date 사용)
     */
    public void checkVerifiedEmailToday(String email) {
        // 1. 회원 이메일인지 확인
        UserEntity existingUser = userRepository.selectByEmail(email);
        if (existingUser != null) {
            // 회원이면 인증 완료로 처리
            return;
        }

        // 2. 비회원이면 오늘 인증된 이메일인지 확인 (DB의 current_date 사용)
        EmailGuestEntity emailGuest = emailGuestRepository.selectTodayVerifiedEmail(email);
        if (emailGuest == null) {
            throw new PetCrownException(EMAIL_NOT_VERIFIED_TODAY);
        }
    }

    /**
     * 오늘 인증된 이메일인지 확인 (boolean 반환 버전, DB의 current_date 사용)
     */
    public boolean isVerifiedEmailToday(String email) {
        // 1. 회원 이메일인지 확인
        UserEntity existingUser = userRepository.selectByEmail(email);
        if (existingUser != null) {
            // 회원이면 항상 true (회원은 별도 인증 불필요)
            return true;
        }

        // 2. 비회원이면 오늘 인증된 이메일인지 확인 (DB의 current_date 사용)
        EmailGuestEntity emailGuest = emailGuestRepository.selectTodayVerifiedEmail(email);
        return emailGuest != null;
    }
}