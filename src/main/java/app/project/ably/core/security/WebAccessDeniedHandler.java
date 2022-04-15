package app.project.ably.core.security;

import app.project.ably.core.handler.exception.ErrorCode;
import app.project.ably.core.web.response.RestResponse;
import com.google.gson.Gson;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class WebAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.FORBIDDEN_EXCEPTION;
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
