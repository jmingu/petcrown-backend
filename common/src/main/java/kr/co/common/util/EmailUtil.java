package kr.co.common.util;

import kr.co.common.dto.EmailContentDto;

public class EmailUtil {

    private EmailUtil() {} // 인스턴스화 방지


    public static EmailContentDto generateEmailContent(String verificationCode) {
        String title = "PET CROWN 인증메일입니다.";
        String content = "<html>"
                + "<head>"
                + "<style>"
                + "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; text-align: center; padding: 40px; }"
                + "  .container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); max-width: 400px; margin: auto; }"
                + "  h1 { color: #333; }"
                + "  p { font-size: 14px; color: #555; }"
                + "  .code-box { font-size: 24px; font-weight: bold; color: #1a73e8; background: #eef2ff; padding: 10px; border-radius: 5px; display: inline-block; margin: 10px 0; }"
                + "  .footer { font-size: 12px; color: grey; margin-top: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "  <h2>PET CROWN 인증 코드</h2>"
                + "  <p>아래 코드를 홈페이지에 입력하세요.</p>"
                + "  <div class='code-box'>" + verificationCode + "</div>"
                + "  <p>이 인증 코드는 일정 시간 후 만료됩니다.</p>"
                + "  <div class='footer'>"
                + "    <p>※본 메일은 자동응답 메일이므로 회신하지 마세요.</p>"
                + "  </div>"
                + "</div>"
                + "</body>"
                + "</html>";

        return EmailContentDto.builder().subject(title).body(content).build();
    }

    /**
     * 투표 이메일 인증을 위한 HTML 콘텐츠 생성
     */
    public static EmailContentDto generateVotingEmailContent(String email, String encryptedToken, String frontendUrl) {
        try {
            String title = "PET CROWN 투표 인증메일입니다.";
            String encodedEmail = java.net.URLEncoder.encode(email, "UTF-8");
            String encodedToken = java.net.URLEncoder.encode(encryptedToken, "UTF-8");
            String verificationUrl = frontendUrl + "/verify-voting-email?email=" + encodedEmail + "&token=" + encodedToken;

        String content = "<html>"
                + "<head>"
                + "<style>"
                + "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; text-align: center; padding: 40px; }"
                + "  .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); max-width: 500px; margin: auto; }"
                + "  h1 { color: #333; }"
                + "  p { font-size: 16px; color: #555; line-height: 1.6; }"
                + "  .btn { display: inline-block; background-color: #1a73e8; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }"
                + "  .btn:hover { background-color: #155ab7; }"
                + "  .token-info { font-size: 12px; color: #777; background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0; word-break: break-all; }"
                + "  .footer { font-size: 12px; color: grey; margin-top: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "  <h2>🐾 PET CROWN 투표 인증</h2>"
                + "  <p>안녕하세요!<br>오늘 투표에 참여할 수 있는 권한을 부여받기 위한 인증 메일입니다.</p>"
                + "  <p>아래 버튼을 클릭하여 인증을 완료해주세요.</p>"
                + "  <a href='" + verificationUrl + "' class='btn'>투표 인증 완료하기</a>"
                + "  <p><strong>주의사항:</strong></p>"
                + "  <p>• 이 인증은 오늘만 유효합니다<br>"
                + "  • 인증 완료 후 오늘 하루 동안 투표 참여가 가능합니다<br>"
                + "  • 본인이 요청하지 않았다면 이 메일을 무시해주세요</p>"
                + "  <div class='footer'>"
                + "    <p>※본 메일은 자동응답 메일이므로 회신하지 마세요.</p>"
                + "  </div>"
                + "</div>"
                + "</body>"
                + "</html>";

            return EmailContentDto.builder().subject(title).body(content).build();
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("URL encoding failed", e);
        }
    }

    /**
     * 임시 비밀번호 발급 이메일 콘텐츠 생성
     */
    public static EmailContentDto generateTemporaryPasswordEmail(String temporaryPassword) {
        String title = "PET CROWN 임시 비밀번호 발급";
        String content = "<html>"
                + "<head>"
                + "<style>"
                + "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; text-align: center; padding: 40px; }"
                + "  .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); max-width: 500px; margin: auto; }"
                + "  h1 { color: #333; }"
                + "  p { font-size: 16px; color: #555; line-height: 1.6; }"
                + "  .password-box { font-size: 24px; font-weight: bold; color: #1a73e8; background: #eef2ff; padding: 15px; border-radius: 5px; display: inline-block; margin: 20px 0; letter-spacing: 2px; }"
                + "  .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 20px 0; text-align: left; }"
                + "  .footer { font-size: 12px; color: grey; margin-top: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "  <h2>🔑 임시 비밀번호 발급</h2>"
                + "  <p>비밀번호 찾기 요청에 따라 임시 비밀번호를 발급해드립니다.</p>"
                + "  <div class='password-box'>" + temporaryPassword + "</div>"
                + "  <div class='warning'>"
                + "    <p><strong>⚠️ 보안을 위한 안내사항</strong></p>"
                + "    <p>• 로그인 후 반드시 비밀번호를 변경해주세요<br>"
                + "    • 임시 비밀번호는 타인에게 노출되지 않도록 주의하세요<br>"
                + "    • 본인이 요청하지 않았다면 즉시 고객센터로 문의해주세요</p>"
                + "  </div>"
                + "  <div class='footer'>"
                + "    <p>※본 메일은 자동응답 메일이므로 회신하지 마세요.</p>"
                + "  </div>"
                + "</div>"
                + "</body>"
                + "</html>";

        return EmailContentDto.builder().subject(title).body(content).build();
    }
}
