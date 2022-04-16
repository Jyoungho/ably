package app.project.ably.auth.service;

import app.project.ably.auth.dto.*;
import app.project.ably.auth.entity.AuthToken;
import app.project.ably.auth.entity.PersonalIdentification;
import app.project.ably.auth.repository.AuthTokenRepository;
import app.project.ably.auth.repository.PersonalIdentificationRepository;
import app.project.ably.core.config.CipherService;
import app.project.ably.core.handler.exception.BizException;
import app.project.ably.core.security.JwtTokenService;
import app.project.ably.user.entity.User;
import app.project.ably.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final PersonalIdentificationRepository personalIdentificationRepository;

    /**
     * 아이디, 전화번호, 주민등록번호 중 1개를 선택하여 로그인가능
     * 로그인요청 시 accessToken, refreshToken 갱신 후 반환
     * 예외처리 : 아이디 비밀번호 잘못입력
     */
    public AuthTokenDTO login(AuthLoginDTO authLoginDTO) {
        User user;
        if (authLoginDTO.getLoginId() != null) {
            user = userRepository.findByLoginId(authLoginDTO.getLoginId())
                    .orElseThrow(() -> BizException.
                            withUserMessageKey("exception.auth.not.correct")
                            .build());
        } else if (authLoginDTO.getPhoneNumber() != null) {
            user = userRepository.findByPhoneNumber(authLoginDTO.getPhoneNumber())
                    .orElseThrow(() -> BizException.
                            withUserMessageKey("exception.auth.not.correct")
                            .build());
        } else if (authLoginDTO.getRegNo() != null) {
            user = userRepository.findByRegNo(authLoginDTO.getRegNo())
                    .orElseThrow(() -> BizException.
                            withUserMessageKey("exception.auth.not.correct")
                            .build());
        } else {
            throw BizException
                    .withUserMessageKey("exception.auth.not.correct")
                    .build();
        }

        if (!passwordEncoder.matches(authLoginDTO.getPassword(), user.getPassword())) {
            throw BizException.
                    withUserMessageKey("exception.auth.not.correct")
                    .build();
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getLoginId(),
                authLoginDTO.getPassword()));

        return createTokenReturn(user);
    }

    /**
     * refreshToken 정보를 기반으로 accessToken 생성
     * 예외처리 : 요청 refreshToken 이 DB refreshToken 과 값이 일치하지 않는 경우
     * 예외처리 : 요청 refreshTokenId 가 DB 에 존재하지 않을 경우
     * 예외처리 : refreshToken 이 유효하지 않을 경우
     */
    public AuthTokenDTO newAccessToken(AuthGetNewAccessTokenDTO authGetNewAccessTokenDTO, HttpServletRequest request) {
        Optional<AuthToken> byId = authTokenRepository.findById(authGetNewAccessTokenDTO.getId());

        // 요청 refreshToken
        String requestRefreshToken = authGetNewAccessTokenDTO.getRefreshToken();

        if (byId.isPresent()) {
            String refreshToken = byId.get().getRefreshToken();

            // 요청 refreshToken 과 DB refreshToken 값이 일치하지 않으면 예외처리
            if (!refreshToken.equals(requestRefreshToken)) {
                throw BizException
                        .withUserMessageKey("exception.refresh.token.not.equal")
                        .build();
            }

            // refreshToken 이 유효할 경우 accessToken 반환
            if (jwtTokenService.validateJwtToken(request, refreshToken)) {
                String loginId = jwtTokenService.getUserInfo(refreshToken);
                User user = userRepository.findByLoginId(loginId)
                        .orElseThrow(() -> BizException
                                .withUserMessageKey("exception.user.not.found")
                                .build());

                return createTokenReturn(user);

            } else {
                // refreshToken 유효하지 않을 경우 예외처리
                throw BizException
                        .withUserMessageKey("exception.refresh.token.not.validate")
                        .build();
            }
        } else {
            // refreshToken 을 찾을 수 없음
            throw BizException
                    .withUserMessageKey("exception.refresh.token.not.found")
                    .build();
        }
    }

    /**
     * loginId 기반으로 accessToken, refreshToken 생성
     */
    public AuthTokenDTO createTokenReturn(User user) {
        String accessToken = jwtTokenService.createAccessToken(user);
        AuthTokenDTO authTokenDTO = jwtTokenService.createRefreshToken(user);
        AuthToken authToken;

        // findByUser 테스트 해보기
        Optional<AuthToken> byUserId = authTokenRepository.findByUser_Id(user.getId());
        if (byUserId.isPresent()) {
            authToken = byUserId.get();
            authToken.updateToken(
                    accessToken,
                    authTokenDTO.getRefreshToken(),
                    authTokenDTO.getRefreshTokenExpirationDate()
            );
        } else {
            authToken = AuthToken.builder()
                    .user(user)
                    .accessToken(accessToken)
                    .refreshToken(authTokenDTO.getRefreshToken())
                    .refreshTokenExpirationDate(authTokenDTO.getRefreshTokenExpirationDate())
                    .build();
        }

        return AuthTokenDTO.from(authTokenRepository.save(authToken));
    }

    /**
     * 핸드폰 인증 메세지
     */
    public PersonalIdentificationDTO sendMessage(PersonalIdentificationSendDTO personalIdentificationSendDTO) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            sb.append((int) (Math.random() * 10));
        }

        String verificationCode = sb.toString();

        LocalDateTime expirationDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(3);

        PersonalIdentification personalIdentification = PersonalIdentification.builder()
                .verificationCode(verificationCode)
                .phoneNumber(personalIdentificationSendDTO.getPhoneNumber())
                .certificated(false)
                .expirationDate(expirationDate)
                .build();

        personalIdentificationRepository.save(personalIdentification);

        return PersonalIdentificationDTO.from(personalIdentification);
    }

    /**
     * 핸드폰 인증
     */
    public boolean checkPhoneNumber(PersonalIdentificationCheckDTO personalIdentificationCheckDTO) {
        String verificationCode = personalIdentificationCheckDTO.getVerificationCode();
        String phoneNumber = personalIdentificationCheckDTO.getPhoneNumber();

        PersonalIdentification personalIdentification = personalIdentificationRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.auth.phoneNumber.not.found")
                        .build());

        // 인증코드와 만료시간 이전일 경우 승인
        if (personalIdentification.getVerificationCode().equals(verificationCode) &&
                personalIdentification.getExpirationDate().isAfter(LocalDateTime.now(ZoneId.of("Asia/Seoul")))) {
            personalIdentification.certificate(true, LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(1));
            personalIdentificationRepository.save(personalIdentification);
            return true;
        }
        return false;
    }
}
