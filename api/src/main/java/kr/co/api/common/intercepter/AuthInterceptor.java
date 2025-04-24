package kr.co.api.common.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.common.enums.BusinessCode;
import kr.co.common.enums.CodeEnum;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod method)) {
            return true; // 컨트롤러 메서드가 아니면 통과
        }

        // SecurityContextHolder에서 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // @AuthRequired 어노테이션 확인
        AuthRequired authRequired = method.getMethodAnnotation(AuthRequired.class);


        // 이미 인증된 요청이면 통과
        if (authentication.getPrincipal() != null) {
            return true; // 인증 통과
        }

        // 인증 객체가 null인데 @AuthRequired가 선언이 안되어있으면 에러
        if (authentication.getPrincipal() == null && authRequired == null) {
            throw new PetCrownException(CodeEnum.AUTHENTICATION_REQUIRED);
        }

        // 인증 객체가 null인데 @AuthRequired(authSkip = true)면 통과
        if (authentication.getPrincipal() == null && authRequired.authSkip() == true) {
            return true; // 인증 통과
        } else {
            throw new PetCrownException(CodeEnum.AUTHENTICATION_REQUIRED);
        }

    }
}
