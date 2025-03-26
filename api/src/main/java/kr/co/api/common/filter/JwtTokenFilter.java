package kr.co.api.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.api.common.util.CryptoUtil;
import kr.co.api.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 로그인은 제외
        if (request.getRequestURI().startsWith("/api/oauth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증이 증명된 헤더
        final String header = request.getHeader("X-Auth-Status");

        if (header == null ) { // 띄어쓰기 있음
            log.error("Error header");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String decrypt = CryptoUtil.decrypt(header);

            // controller에서 사용자 정보 조회할 수 있음
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    decrypt, null, null
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            // 여기에 context에 넣어 controller까지 넘겨 사용할 수 있음
            SecurityContextHolder.getContext().setAuthentication(authentication); // 여기까지 설정해야 시큐리티 정상으로 넘어간다.

        } catch (Exception e) {
            log.error("Error token");
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}