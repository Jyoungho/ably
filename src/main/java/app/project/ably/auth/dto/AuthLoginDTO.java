package app.project.ably.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class AuthLoginDTO {

    @ApiModelProperty(value = "아이디", example = "test")
    private String loginId;

    @ApiModelProperty(value = "핸드폰 번호", example = "010-1234-5678")
    private String phoneNumber;

    @ApiModelProperty(value = "주민등록번호", example = "920910-1234567")
    private String regNo;

    @ApiModelProperty(value = "비밀번호", example = "test1234", required = true)
    @NotEmpty
    private String password;

}
