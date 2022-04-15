package app.project.ably.auth;

import app.project.ably.auth.dto.AuthTokenDTO;
import app.project.ably.auth.dto.PersonalIdentificationCheckDTO;
import app.project.ably.auth.dto.PersonalIdentificationDTO;
import app.project.ably.auth.dto.PersonalIdentificationSendDTO;
import app.project.ably.config.BaseMvcTest;
import app.project.ably.core.web.Path;
import app.project.ably.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("인증")
public class AuthTest extends BaseMvcTest {

    @Test
    @Transactional
    @Order(1)
    @DisplayName("핸드폰 인증 메세지 전송")
    public void send_message() throws Exception {
        // given
        PersonalIdentificationSendDTO personalIdentificationSendDTO = PersonalIdentificationSendDTO.builder()
                .phoneNumber("010-1234-5678")
                .build();


        String requestBody = objectMapper.writeValueAsString(personalIdentificationSendDTO);

        // when
        mockMvc.perform(post(Path.AUTH_CHECK_PHONE_NUMBER_MESSAGE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.verificationCode").isString())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.send.message")));
    }

    @Test
    @Transactional
    @Order(2)
    @DisplayName("핸드폰 인증")
    public void check_phone_number() throws Exception {
        // given
        PersonalIdentificationSendDTO personalIdentificationSendDTO = PersonalIdentificationSendDTO.builder()
                .phoneNumber("010-1234-5678")
                .build();

        PersonalIdentificationDTO personalIdentificationDTO = authService.sendMessage(personalIdentificationSendDTO);

        PersonalIdentificationCheckDTO personalIdentificationCheckDTO = PersonalIdentificationCheckDTO.builder()
                .verificationCode(personalIdentificationDTO.getVerificationCode())
                .phoneNumber(personalIdentificationDTO.getPhoneNumber())
                .build();

        String requestBody = objectMapper.writeValueAsString(personalIdentificationCheckDTO);

        // when
        mockMvc.perform(post(Path.AUTH_CHECK_PHONE_NUMBER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(true))
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.check.phoneNumber")));
    }

    @Test
    @Transactional
    @Order(3)
    @DisplayName("로그인 - 아이디, 비밀번호")
    public void login_with_id() throws Exception {
        // given
        User testUser = getTestUser();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("loginId", testUser.getLoginId());
        requestData.put("password", testUserPassword);

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.login")));
    }

    @Test
    @Transactional
    @Order(4)
    @DisplayName("로그인 - 핸드폰번호, 비밀번호")
    public void login_with_phoneNumber() throws Exception {
        // given
        User testUser = getTestUser();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("phoneNumber", testUser.getPhoneNumber());
        requestData.put("password", testUserPassword);

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.login")));
    }

    @Test
    @Transactional
    @Order(5)
    @DisplayName("로그인 - 주민등록번호, 비밀번호")
    public void login_with_regNo() throws Exception {
        // given
        User testUser = getTestUser();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("regNo", testUser.getRegNo());
        requestData.put("password", testUserPassword);

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.login")));
    }

    @Test
    @Transactional
    @Order(6)
    @DisplayName("토큰재발급")
    public void getNewAccessToken() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getToken();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", authTokenDTO.getRefreshId());
        requestData.put("refreshToken", authTokenDTO.getRefreshToken());

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.access.token.create.again")));
    }

    @Test
    @Transactional
    @Order(7)
    @DisplayName("토큰재발급(예외) - DB 데이터와 refreshToken 다름")
    public void getNewAccessTokenExceptData() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getToken();

        Map<String, Object> requestExceptData = new HashMap<>();
        requestExceptData.put("id", authTokenDTO.getRefreshId());
        requestExceptData.put("refreshToken", "ss");

        String requestBody = objectMapper.writeValueAsString(requestExceptData);

        // when
        mockMvc.perform(post(Path.AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.refresh.token.not.equal")));
    }

    @Test
    @Transactional
    @Order(8)
    @DisplayName("토큰재발급(예외) - refreshToken 만료")
    public void getNewAccessTokenExceptJwt() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getExpiredRefreshToken();

        Map<String, Object> requestExceptData = new HashMap<>();
        requestExceptData.put("id", authTokenDTO.getRefreshId());
        requestExceptData.put("refreshToken", authTokenDTO.getRefreshToken());

        String requestBody = objectMapper.writeValueAsString(requestExceptData);

        // when
        mockMvc.perform(post(Path.AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.refresh.token.not.validate")));
    }

    @Test
    @Transactional
    @Order(9)
    @DisplayName("토큰재발급(예외) - DB 조회불가")
    public void getNewAccessTokenExceptNotFound() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getToken();

        Map<String, Object> requestExceptData = new HashMap<>();
        requestExceptData.put("id", Long.MAX_VALUE);
        requestExceptData.put("refreshToken", authTokenDTO.getRefreshToken());

        String requestBody = objectMapper.writeValueAsString(requestExceptData);

        // when
        mockMvc.perform(post(Path.AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.refresh.token.not.found")));
    }
}
