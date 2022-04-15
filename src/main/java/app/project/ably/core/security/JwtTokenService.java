package app.project.ably.core.security;

import app.project.ably.auth.dto.AuthLoginDTO;
import app.project.ably.auth.dto.AuthTokenDTO;
import app.project.ably.user.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenService {

    @Value("${app.security.jwt.secretKey}")
    private String secretKey;

    @Value("${app.security.jwt.accessExpireTime}")
    private long accessExpireTime;

    @Value("${app.security.jwt.refreshExpireTime}")
    private long refreshExpireTime;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    /** create accessToken */
    public String createAccessToken(User user) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("loginId", user.getLoginId());

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + accessExpireTime);

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("user")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /** create refreshToken */
    public AuthTokenDTO createRefreshToken(User user) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("loginId", user.getLoginId());

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + refreshExpireTime);
        LocalDateTime refreshTokenExpirationDate = expiration.toInstant()
                                                            .atZone(ZoneId.of("Asia/Seoul"))
                                                            .plusNanos(1000L) // 소수점 6자리 맞춰주기 위하여
                                                            .toLocalDateTime();

        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("user")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return AuthTokenDTO.builder()
                .refreshToken(jwt)
                .refreshTokenExpirationDate(refreshTokenExpirationDate)
                .build();
    }

    /** get Authentication form token */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(getUserInfo(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /** get loginId from token */
    public String getUserInfo(String token) {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("loginId");
    }

    /** get token from request headers */
    public String getToken(HttpServletRequest request) {
        // header 에 Authentication 을 Bearer 로 token 받기
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * validate jwt token
     * */
    public boolean validateJwtToken(ServletRequest request, String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            request.setAttribute("validToken", true);
            return true;
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", "MalformedJwtException");
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", "ExpiredJwtException");
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", "UnsupportedJwtException");
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", "IllegalArgumentException");
        }
        return false;
    }
}
