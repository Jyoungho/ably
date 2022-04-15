package app.project.ably.config;

import app.project.ably.auth.dto.AuthLoginDTO;
import app.project.ably.auth.dto.AuthTokenDTO;
import app.project.ably.auth.entity.AuthToken;
import app.project.ably.auth.repository.AuthTokenRepository;
import app.project.ably.auth.repository.PersonalIdentificationRepository;
import app.project.ably.auth.service.AuthService;
import app.project.ably.core.config.CipherService;
import app.project.ably.core.config.MessageComponent;
import app.project.ably.core.security.JwtTokenService;
import app.project.ably.user.entity.User;
import app.project.ably.user.enums.UserRoleType;
import app.project.ably.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EnableMockMvc
public class BaseMvcTest extends BaseTest{

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MessageComponent messageComponent;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthTokenRepository authTokenRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected JwtTokenService jwtTokenService;

    @Autowired
    protected CipherService cipherService;

    @Autowired
    protected PersonalIdentificationRepository personalIdentificationRepository;

    protected final String testUserPassword = "test1234";

    protected final String checkEmptyValidateMessage = "must not be empty";

    protected AuthTokenDTO authTokenDTO = null;

    @Value("${app.security.jwt.secretKey}")
    protected String secretKey;

    protected User getTestUser() {
        return userRepository.findByLoginId("test")
                .orElseGet(this::createTestUser);
    }

    protected User createTestUser() {
        User user = User.builder()
                .loginId("test")
                .password(passwordEncoder.encode(testUserPassword))
                .name("홍길동")
                .regNo(cipherService.encrypt("920910-1234567"))
                .phoneNumber("010-1234-5678")
                .userRoleType(UserRoleType.USER)
                .build();
        return userRepository.save(user);
    }

    protected AuthTokenDTO getToken() {
        if (authTokenDTO == null){
            User testUser = getTestUser();
            AuthLoginDTO authLoginDTO = AuthLoginDTO.builder()
                    .loginId(testUser.getLoginId())
                    .password(testUserPassword)
                    .build();
            authTokenDTO = authService.login(authLoginDTO);
        }
        return authTokenDTO;
    }

    protected AuthTokenDTO getExpiredRefreshToken() {
        User testUser = getTestUser();

        String accessToken = jwtTokenService.createAccessToken(testUser);
        AuthTokenDTO authTokenDTO = createRefreshTokenExcept(testUser);

        AuthToken authToken = AuthToken.builder()
                .user(testUser)
                .accessToken(accessToken)
                .refreshToken(authTokenDTO.getRefreshToken())
                .refreshTokenExpirationDate(authTokenDTO.getRefreshTokenExpirationDate())
                .build();

        return AuthTokenDTO.from(authTokenRepository.save(authToken));
    }

    private AuthTokenDTO createRefreshTokenExcept(User user) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("loginId", user.getLoginId());

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() - 1);
        LocalDateTime refreshTokenExpirationDate = expiration.toInstant()
                .atZone(ZoneId.systemDefault())
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
}
