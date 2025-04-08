package kr.co.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    /**
     * 쿠키 value 가져오는 메서드
     */
    public static String getCookieValue(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 쿠키 셋팅
     */
    public static void setTokenCookies(
            HttpServletResponse response,
            String accessToken,
            String refreshToken,
            boolean isSecure,
            int accessTokenAge,
            int refreshTokenAge
    ) {


        Cookie accessTokenCookie = new Cookie("A_ID", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(isSecure);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(accessTokenAge);

        Cookie refreshTokenCookie = new Cookie("R_ID", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(isSecure);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(refreshTokenAge);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
