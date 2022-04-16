package app.project.ably.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class PersonalIdentificationSendDTO {

    @ApiModelProperty(value = "phoneNumber", example = "010-1234-5678", required = true)
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "{exception.validate.phoneNumber}")
    @NotEmpty
    private String phoneNumber;
}
