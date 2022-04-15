package app.project.ably.core.security;

import app.project.ably.core.handler.exception.ErrorCode;
import app.project.ably.core.web.response.RestResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String exception = String.valueOf(request.getAttribute("exception"));
        ErrorCode errorCode;

        // JWT 토큰만료 예외처리
        if (exception.equals("ExpiredJwtException")) {
            errorCode = ErrorCode.EXPIRED_JWT_TOKEN_EXCEPTION;
            setResponse(response, errorCode);
            return;
        }

        errorCode = ErrorCode.UNAUTHORIZED_EXCEPTION;
        setResponse(response, errorCode);
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        Gson gson = new Gson();
        String responseBody = gson.toJson(RestResponse
                .withData(new HashMap<>())
                .withUserMessage(errorCode.getDescription())
                .build());

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(responseBody);
    }
}
