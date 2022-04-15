package app.project.ably.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AuthGetNewAccessTokenDTO {

    @ApiModelProperty(value = "Refresh Token id", example = "1", required = true)
    @NotNull
    private Long id;

    @ApiModelProperty(value = "Refresh Token", required = true)
    @NotEmpty
    private String refreshToken;
}
