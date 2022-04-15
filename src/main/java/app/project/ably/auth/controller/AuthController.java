package app.project.ably.auth.controller;

import app.project.ably.auth.dto.*;
import app.project.ably.auth.service.AuthService;
import app.project.ably.core.web.Path;
import app.project.ably.core.web.response.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "Auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "로그인 성공"),
            @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "로그인 실패")
    })
    @PostMapping(Path.AUTH_LOGIN)
    public RestResponse<AuthTokenDTO> login(@Validated @RequestBody AuthLoginDTO authLoginDTO) {
        AuthTokenDTO authTokenDTO = authService.login(authLoginDTO);

        return RestResponse
                .withData(authTokenDTO)
                .withUserMessageKey("success.auth.login")
                .build();
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiResponses(value = {
        @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "발급성공"),
        @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "발급실패")
    })
    @PostMapping(Path.AUTH_TOKEN)
    public RestResponse<AuthTokenDTO> getNewAccessToken(
            HttpServletRequest request,
            @Validated @RequestBody AuthGetNewAccessTokenDTO authGetNewAccessTokenDTO) {
        AuthTokenDTO authTokenDTO = authService.newAccessToken(authGetNewAccessTokenDTO, request);

        return RestResponse
                .withData(authTokenDTO)
                .withUserMessageKey("success.auth.access.token.create.again")
                .build();
    }

    @ApiOperation(value = "핸드폰번호 인증")
    @ApiResponses(value = {
            @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "인증성공"),
            @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "인증실패")
    })
    @PostMapping(Path.AUTH_CHECK_PHONE_NUMBER)
    public RestResponse<Boolean> checkPhoneNumber(@Validated @RequestBody PersonalIdentificationCheckDTO personalIdentificationCheckDTO) {

        return RestResponse
                .withData(authService.checkPhoneNumber(personalIdentificationCheckDTO))
                .withUserMessageKey("success.auth.check.phoneNumber")
                .build();
    }

    @ApiOperation(value = "핸드폰번호 인증 메세지 전송")
    @ApiResponses(value = {
            @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "인증성공"),
            @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "인증실패")
    })
    @PostMapping(Path.AUTH_CHECK_PHONE_NUMBER_MESSAGE)
    public RestResponse<PersonalIdentificationDTO> sendMessageToPhone(@Validated @RequestBody PersonalIdentificationSendDTO personalIdentificationSendDTO) {

        return RestResponse
                .withData(authService.sendMessage(personalIdentificationSendDTO))
                .withUserMessageKey("success.auth.send.message")
                .build();
    }
}
