package kr.co.api.common.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@RequiredArgsConstructor
public class JwtProperty {

    private final String secretKey;
    private final String tokenClaimsKey;
    private final String tokenAccessDecryptKey;
    private final String tokenRefreshDecryptKey;
    private final int expiredTime;
    private final int expiredRefreshTime;
}
