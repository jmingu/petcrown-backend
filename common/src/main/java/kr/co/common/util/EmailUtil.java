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

        return EmailContentDto.builder().title(title).content(content).build();
    }
}
