package app.project.ably.user;

import app.project.ably.auth.dto.AuthTokenDTO;
import app.project.ably.auth.entity.AuthToken;
import app.project.ably.auth.entity.PersonalIdentification;
import app.project.ably.config.BaseMvcTest;
import app.project.ably.core.handler.exception.ErrorCode;
import app.project.ably.core.web.Path;
import app.project.ably.user.dto.UpdatePasswordDTO;
import app.project.ably.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("사용자")
public class UserTest extends BaseMvcTest {

    @Test
    @Transactional
    @Order(1)
    @DisplayName("회원가입")
    public void createUser() throws Exception {
        // given
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("loginId", "test");
        requestData.put("password", testUserPassword);
        requestData.put("name", "회원가입 테스트 계정");
        requestData.put("regNo", "920910-1234567");
        requestData.put("phoneNumber", "010-1234-5678");

        PersonalIdentification personalIdentification = PersonalIdentification.builder()
                .certificated(true)
                .certificationDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(1))
                .phoneNumber("010-1234-5678")
                .build();
        personalIdentificationRepository.save(personalIdentification);

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.USER_SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.user.create")));
    }

    @Test
    @Transactional
    @Order(2)
    @DisplayName("회원가입(예외) - 본인인증 절차 선행필요")
    public void createUser_exception_need_certificate() throws Exception {
        // given case
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("loginId", "test");
        requestData.put("password", testUserPassword);
        requestData.put("name", "회원가입 테스트 계정");
        requestData.put("regNo", "920910-1234567");
        requestData.put("phoneNumber", "010-1234-5678");

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.USER_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.auth.need.check.phoneNumber")));
    }

    @Test
    @Transactional
    @Order(3)
    @DisplayName("회원가입(예외) - validate check empty")
    public void createUser_exception_validate_loginId() throws Exception {
        // given case
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("loginId", null);
        requestData.put("password", null);
        requestData.put("name", null);
        requestData.put("regNo", null);
        requestData.put("phoneNumber", null);

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.USER_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("data.loginId").value(checkEmptyValidateMessage))
                .andExpect(jsonPath("data.password").value(checkEmptyValidateMessage))
                .andExpect(jsonPath("data.name").value(checkEmptyValidateMessage))
                .andExpect(jsonPath("data.regNo").value("올바른 주민등록번호를 입력하여 주시기 바랍니다."))
                .andExpect(jsonPath("data.phoneNumber").value(checkEmptyValidateMessage))
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.validate.need")));
    }

    @Test
    @Transactional
    @Order(4)
    @DisplayName("회원가입(예외) - validate 비밀번호, 주민등록번호, 핸드폰번호 양식")
    public void createUser_exception_validate_password() throws Exception {
        // given case
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("loginId", "test");
        requestData.put("password", "11");
        requestData.put("name", "회원가입 테스트 계정");
        requestData.put("regNo", "920910-1234568");
        requestData.put("phoneNumber", "01012345678");

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.USER_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("data.regNo").value(messageComponent.getMessage("exception.validate.regNo")))
                .andExpect(jsonPath("data.password").value(messageComponent.getMessage("exception.validate.password")))
                .andExpect(jsonPath("data.phoneNumber").value(messageComponent.getMessage("exception.validate.phoneNumber")))
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.validate.need")));
    }

    @Test
    @Transactional
    @Order(5)
    @DisplayName("내정보보기")
    public void getUserInfo() throws Exception {
        // given
        StringBuilder sb = new StringBuilder();
        AuthTokenDTO token = getToken();

        String bearAccessToken = sb.append("Bearer").append(" ").append(token.getAccessToken()).toString();

        // when
        mockMvc.perform(get(Path.USER_INFO)
                .header(HttpHeaders.AUTHORIZATION, bearAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.user.get")));
    }

    @Test
    @Transactional
    @Order(6)
    @DisplayName("내정보보기(예외) - 잘못된 토큰")
    public void getUserInfoError() throws Exception {
        // given
        String bearAccessTokenError = "Bearer sss111";

        // when
        mockMvc.perform(get(Path.USER_INFO)
                .header(HttpHeaders.AUTHORIZATION, bearAccessTokenError)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(ErrorCode.UNAUTHORIZED_EXCEPTION.getDescription()));
    }

    @Test
    @Transactional
    @Order(7)
    @DisplayName("비밀번호변경")
    public void updatePassword() throws Exception {
        // given
        User testUser = getTestUser();
        UpdatePasswordDTO updatePasswordDTO = UpdatePasswordDTO.builder()
                .phoneNumber(testUser.getPhoneNumber())
                .password("newTest1234")
                .build();

        // 본인인증
        PersonalIdentification personalIdentification = PersonalIdentification.builder()
                .certificated(true)
                .certificationDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(1))
                .phoneNumber(testUser.getPhoneNumber())
                .build();
        personalIdentificationRepository.save(personalIdentification);

        // 기존 토큰 생성
        AuthToken authToken = AuthToken.builder()
                .accessToken("eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwibG9naW5JZCI6InRlc3QiLCJleHAiOjE2NTAwMjMwMTB9.EfHYzTXDWFgt79WD2T2K2bk2m2YnLWV0bvyX_a63HrY")
                .user(testUser)
                .refreshToken("eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwibG9naW5JZCI6InRlc3QiLCJleHAiOjE2NTEzMTgxMTB9.cVvGITZqVljlpO3GeOojY0VU9XUi1lXNetPS_q3ujdw")
                .refreshTokenExpirationDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(15))
                .build();

        authTokenRepository.save(authToken);

        String requestBody = objectMapper.writeValueAsString(updatePasswordDTO);

        // when
        mockMvc.perform(post(Path.USER_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.user.update.password")));

        // 토큰 초기화 확인
        AuthToken authTokenResult = authTokenRepository.findById(authToken.getId()).orElse(null);
        assert authTokenResult != null;
        assert authTokenResult.getAccessToken() == null;
        assert authTokenResult.getRefreshToken() == null;
        assert authTokenResult.getRefreshTokenExpirationDate() == null;
    }


    @Test
    @Transactional
    @Order(8)
    @DisplayName("비밀번호변경(예외) - 본인인증 미실시")
    public void updatePassword_exception_need_certification() throws Exception {
        // given
        User testUser = getTestUser();
        UpdatePasswordDTO updatePasswordDTO = UpdatePasswordDTO.builder()
                .phoneNumber(testUser.getPhoneNumber())
                .password("newTest1234")
                .build();

        String requestBody = objectMapper.writeValueAsString(updatePasswordDTO);

        // when
        mockMvc.perform(post(Path.USER_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.auth.need.check.phoneNumber")));
    }

    @Test
    @Transactional
    @Order(9)
    @DisplayName("비밀번호변경(예외) - 토큰 정보 초기화 실패")
    public void updatePassword_exception_need_tokenInfo() throws Exception {
        // given
        User testUser = getTestUser();
        UpdatePasswordDTO updatePasswordDTO = UpdatePasswordDTO.builder()
                .phoneNumber(testUser.getPhoneNumber())
                .password("newTest1234")
                .build();

        // 본인인증
        PersonalIdentification personalIdentification = PersonalIdentification.builder()
                .certificated(true)
                .certificationDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(1))
                .phoneNumber(testUser.getPhoneNumber())
                .build();
        personalIdentificationRepository.save(personalIdentification);

        String requestBody = objectMapper.writeValueAsString(updatePasswordDTO);

        // when
        mockMvc.perform(post(Path.USER_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.token.not.found")));
    }
}
