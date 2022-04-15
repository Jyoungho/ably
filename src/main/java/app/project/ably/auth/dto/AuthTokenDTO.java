package app.project.ably.auth.dto;

import app.project.ably.auth.entity.AuthToken;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class AuthTokenDTO {

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "accessToken", example = "1")
    private Long refreshId;

    @ApiModelProperty(value = "refreshToken")
    private String refreshToken;

    @ApiModelProperty(value = "refreshTokenExpirationDate", example = "2022-04-22T01:17:57.390001")
    private LocalDateTime refreshTokenExpirationDate;

    public static AuthTokenDTO from(AuthToken authToken) {
        return AuthTokenDTO.builder()
                .accessToken(authToken.getAccessToken())
                .refreshId(authToken.getId())
                .refreshToken(authToken.getRefreshToken())
                .refreshTokenExpirationDate(authToken.getRefreshTokenExpirationDate())
                .build();
    }
}
