package kr.co.api.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.util.JwtUtil;
import kr.co.common.dto.CommonResponseDto;
import kr.co.common.enums.CodeEnum;
import kr.co.common.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
            "/swagger-ui/", "/v3/api-docs", "/service-worker.js", "/encrypt"
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
        log.debug("authorization => {}", authorization);
        // 만료되었거나 없는 경우 controller의 어노테이션에서 판단(로그인 필요없는 url 또는 쿠키 만료)
        if (authorization == null || authorization.isBlank()) {

            securityContextHolderInfo(request, null, null, null);
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 가져오기 (Bearer를 뺸다)
        String encryptedAccessToken = authorization.split(" ")[1].trim();


        String accessToken = null;
        try {
            // 쿠키에 담긴 암호화된 accessToken 복호화
            accessToken = CryptoUtil.decrypt(encryptedAccessToken, jwtProperty.getTokenAccessDecryptKey());
        } catch (Exception e) {
            sendErrorResponse(response, CodeEnum.AUTHENTICATION_ERROR);
//            throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
        }

        // 토큰유효 확인
        if (jwtUtil.isExpired(accessToken, jwtProperty.getSecretKey())) {
            //  응답번호 440 토큰 만료
            sendErrorResponse(response, CodeEnum.INVALID_TOKEN);
//            throw new PetCrownException(CodeEnum.INVALID_TOKEN);
        }

        // 엑세스 토큰인지 확인
        String type = null;
        try {
            type = CryptoUtil.decrypt(jwtUtil.getUserName(accessToken, jwtProperty.getSecretKey(), "type"), jwtProperty.getTokenClaimsKey());
            log.debug("type ==> {}", type);
            if (!type.equals("access")) {
//                throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
                sendErrorResponse(response, CodeEnum.AUTHENTICATION_ERROR);
                return;
            }
        } catch (ExpiredJwtException e) {
            // 토큰 만료 시 440 응답
            sendErrorResponse(response, CodeEnum.INVALID_TOKEN);
            return;
        } catch (Exception e) {
//            throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
            sendErrorResponse(response, CodeEnum.AUTHENTICATION_ERROR);
            return;
        }


        // 토큰의 사용자 아이디 가져오기
        String identifier = null;
        try {
            identifier = jwtUtil.getUserName(accessToken, jwtProperty.getSecretKey(), "identifier");
        } catch (ExpiredJwtException e) {
            // 토큰 만료 시 440 응답
            sendErrorResponse(response, CodeEnum.INVALID_TOKEN);
            return;
        } catch (Exception e) {
            sendErrorResponse(response, CodeEnum.AUTHENTICATION_ERROR);
            return;
        }

        log.debug("identifier ==> {}", identifier);
        String userId = null;
        try {
            userId = CryptoUtil.decrypt(identifier, jwtProperty.getTokenClaimsKey());
        } catch (Exception e) {
//            throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
            sendErrorResponse(response, CodeEnum.AUTHENTICATION_ERROR);
            return;
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

    /**
     * 에러 응답을 보내는 메서드
     * 필터에서는 @RestControllerAdvice까지 가지 못하기 때문에
     */
    private void sendErrorResponse(HttpServletResponse response, CodeEnum codeEnum) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json; charset=UTF-8"); // UTF-8 설정
        response.setCharacterEncoding("UTF-8"); // 문자 인코딩 설정
        CommonResponseDto responseDto = new CommonResponseDto(codeEnum.getCode(), codeEnum.getMessageKo(), codeEnum.getMessageEn());
        new ObjectMapper().writeValue(response.getWriter(), responseDto);
    }

}

