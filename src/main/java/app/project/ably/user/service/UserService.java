package app.project.ably.user.service;

import app.project.ably.auth.entity.AuthToken;
import app.project.ably.auth.entity.PersonalIdentification;
import app.project.ably.auth.repository.AuthTokenRepository;
import app.project.ably.auth.repository.PersonalIdentificationRepository;
import app.project.ably.core.config.CipherService;
import app.project.ably.core.handler.exception.BizException;
import app.project.ably.core.security.JwtTokenService;
import app.project.ably.user.dto.UpdatePasswordDTO;
import app.project.ably.user.dto.UserDTO;
import app.project.ably.user.dto.UserRegDTO;
import app.project.ably.user.entity.User;
import app.project.ably.user.enums.UserRoleType;
import app.project.ably.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final CipherService cipherService;
    private final AuthTokenRepository authTokenRepository;
    private final PersonalIdentificationRepository personalIdentificationRepository;


    /**
     * Request Authorization Bearer JWT 토큰으로 User 정보조회
     * 예외조건 : 토큰이 없을 경우
     * 예외조건 : DB 에 해당 user 정보가 없을 경우
     */
    @Transactional
    public UserDTO getUserByToken(HttpServletRequest request) {
        String token = jwtTokenService.getToken(request);
        if (token == null) {
            throw BizException
                    .withUserMessageKey("exception.token.need")
                    .build();
        }

        String loginId = jwtTokenService.getUserInfo(token);
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.user.not.found")
                        .build());

        return UserDTO.fromWithOutPassword(user, cipherService);
    }

    /**
     * 회원가입
     * 예외조건 : 기존 loginId 가 이미 있을 경우
     */
    @Transactional
    public UserDTO signup(UserRegDTO userRegDTO) {
        checkUserRoleType(userRegDTO);
        UserRegDTO encryptUserRegDTO = encryptInfo(userRegDTO);

        // 선행작업으로 핸드폰 인증 필요
        PersonalIdentification personalIdentification = personalIdentificationRepository.findByPhoneNumber(userRegDTO.getPhoneNumber())
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.auth.need.check.phoneNumber")
                        .build());

        if (!personalIdentification.getCertificated() ||
                personalIdentification.getCertificationDate().isBefore(LocalDateTime.now(ZoneId.of("Asia/Seoul")))) {
            throw BizException
                    .withUserMessageKey("exception.auth.need.check.phoneNumber")
                    .build();
        }

        // 기존 회원가입 여부 확인
        userRepository.findByLoginId(userRegDTO.getLoginId())
                .ifPresent(user -> {
                    throw BizException
                            .withUserMessageKey("exception.user.already.exist")
                            .build();
                });

        // 회원가입
        User save = userRepository.save(UserRegDTO.toEntity(encryptUserRegDTO));
        return UserDTO.fromWithOutPassword(save, cipherService);
    }

    public boolean updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        // 본인인증 절차 확인
        PersonalIdentification personalIdentification = personalIdentificationRepository.findByPhoneNumber(updatePasswordDTO.getPhoneNumber())
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.auth.need.check.phoneNumber")
                        .build());

        if (!personalIdentification.getCertificated() ||
                personalIdentification.getCertificationDate().isBefore(LocalDateTime.now(ZoneId.of("Asia/Seoul")))) {
            throw BizException
                    .withUserMessageKey("exception.auth.need.check.phoneNumber")
                    .build();
        }


        // 비밀번호 변경
        User user = userRepository.findByPhoneNumber(updatePasswordDTO.getPhoneNumber())
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.user.not.found")
                        .build());

        user.updatePassword(passwordEncoder.encode(updatePasswordDTO.getPassword()));
        userRepository.save(user);

        // 토큰 정보초기화 (보안)
        AuthToken authToken = authTokenRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.token.not.found")
                        .build());

        authToken.updateToken(null, null, null);

        authTokenRepository.save(authToken);
        return false;
    }

    /** 어드민 권한 생성불가(관리자에게 문의) */
    private void checkUserRoleType(UserRegDTO userRegDTO) {
        if (userRegDTO.getUserRoleType() == null) {
            throw BizException
                    .withUserMessageKey("exception.user.role.type.null")
                    .build();
        }

        if (userRegDTO.getUserRoleType() == UserRoleType.ADMIN) {
            throw BizException
                    .withUserMessageKey("exception.user.create.admin")
                    .build();
        }
    }

    private UserRegDTO encryptInfo(UserRegDTO userRegDTO) {
        String encryptPassword = passwordEncoder.encode(userRegDTO.getPassword());
        String encryptRegNo = cipherService.encrypt(userRegDTO.getRegNo());

        userRegDTO.setPassword(encryptPassword);
        userRegDTO.setRegNo(encryptRegNo);

        return userRegDTO;
    }
}
