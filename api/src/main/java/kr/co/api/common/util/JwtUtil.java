package kr.co.api.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.co.api.common.property.JwtProperty;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperty jwtProperty;

    /**
     * 토큰생성
     */
    public String makeAuthToken(UserEntity userEntity, int tokeTime, String tokenClf) {
        Claims claims = Jwts.claims();
        claims.put("userId", userEntity.getUserId()+"");
        claims.put("tokenClf", tokenClf);

        // 현재 날짜와 시간 가져오기
        Date currentDate = new Date();

        // Calendar 객체 생성 및 현재 날짜 및 시간으로 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        // 분 추가
        calendar.add(Calendar.MINUTE, tokeTime);
        // 변경된 날짜 및 시간 가져오기
        Date newDate = calendar.getTime();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate) // 발생시간
                .setExpiration(newDate) // 만료시간
                .signWith(getKey(jwtProperty.getSecretKey()), SignatureAlgorithm.HS256)
                .compact(); // String으로 만듬 base64로 인코딩;
    }


    // Key값 만들기
    private Key getKey(String key) {
        log.debug("key ==> {}", key);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // 시크릿키로 변환
    }

    /**
     * 토큰 검증
     */
    public boolean isExpired(String token, String key) {
        try {
            log.debug("token ==> {}", token);
            // 토큰 시간확인
            Date expiredDate = Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody().getExpiration(); //body에서 유효시간;
            log.debug("expiredDate ==> {}", expiredDate);
            return expiredDate.before(new Date()); // 현재시간보다 더 전인가
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 경우 true 반환
            return true;
        }
    }

    // 토큰에서 claims 가져온다
    private Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody(); // body가 나온다


    }

    // 토큰에서 정보 가져오기
    public String getUserName(String token, String key) {
        String tokenClf = extractClaims(token, key).get("tokenClf", String.class);
        log.debug("tokenClf ==> {}", tokenClf);
        if (!"refreshToken".equals(tokenClf)) {
            throw new IllegalArgumentException("허용되지 않은 값이 있습니다.");
        }
        return extractClaims(token, key).get("userId", String.class); //body에서 userId가져오기
    }

}