package app.project.ably.core.handler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USERNAME_OR_PASSWORD_NOT_FOUND_EXCEPTION ("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_EXCEPTION( "인증완료 후 이용가능합니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_TOKEN_EXCEPTION( "토큰인증이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_EXCEPTION( "권한이 없습니다.", HttpStatus.FORBIDDEN),
    ;

    private final String description;

    private final HttpStatus status;

    ErrorCode(String description, HttpStatus status) {
        this.description = description;
        this.status = status;
    }
}
