package kr.co.api.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.util.JwtUtil;
import kr.co.common.enums.CodeEnum;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtProperty jwtProperty;
    private final JwtUtil jwtUtil;
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/swagger-ui/", "/v3/api-docs", "/service-worker.js"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        //  필터를 타지 않고 바로 다음 필터로 진행
        if (EXCLUDED_PATHS.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증이 증명된 헤더
        final String authorization = request.getHeader("Authorization");

        // null 값이면 controller 어노테이션으로 재검증
        if (authorization == null ) {
            // null이면 controller에서 어노테이션으로 재 검즘(로그인 필요없는 url)
            securityContextHolderInfo(request, null, null, null);

            filterChain.doFilter(request, response);
            return;
        }
        String getToken = authorization.split(" ")[1].trim();

        // 토큰유효 확인
        if (jwtUtil.isExpired(getToken, jwtProperty.getSecretKey())) {
            //  응답번호 440 토큰 만료
            throw new PetCrownException(CodeEnum.INVALID_TOKEN);
        }

        // 토큰의 사용자 아이디 가져오기
        String identifier = jwtUtil.getUserName(getToken, jwtProperty.getSecretKey(), "identifier");

        log.debug("identifier ==> {}", identifier);
        String userId = null;
        try {
            userId = CryptoUtil.decrypt(identifier, jwtProperty.getTokenDecryptKey());
        } catch (Exception e) {
            throw new PetCrownException("토큰오류");
        }

        log.debug("userId ==> {}", userId);
        // controller에서 사용자 정보 조회할 수 있음
        securityContextHolderInfo(request, userId, null, null);


        filterChain.doFilter(request, response);
    }

    private void securityContextHolderInfo(HttpServletRequest request, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, credentials, authorities
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 여기에 context에 넣어 controller까지 넘겨 사용할 수 있음
        SecurityContextHolder.getContext().setAuthentication(authentication); // 여기까지 설정해야 시큐리티 정상으로 넘어간다.
    }
}