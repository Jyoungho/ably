package app.project.ably.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class PersonalIdentificationCheckDTO {

    @ApiModelProperty(value = "verificationCode", required = true)
    private String verificationCode;

    @ApiModelProperty(value = "phoneNumber", required = true)
    private String phoneNumber;
}
