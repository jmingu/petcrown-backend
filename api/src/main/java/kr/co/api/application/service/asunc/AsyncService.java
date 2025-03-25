package kr.co.api.application.service.asunc;

import kr.co.api.application.port.out.email.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncService {

    private final EmailSenderPort emailSenderPort;

    /**
     * 이메일 전송
     */
    @Async
    public void sendEmailAsync(String toEmail, String subject, String content) {
        emailSenderPort.sendEmail(toEmail, subject, content);
    }
}
