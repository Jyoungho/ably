package app.project.ably.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class UpdatePasswordDTO {

    @ApiModelProperty(value = "핸드폰 번호", example = "01012345678", required = true)
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호를 확인해주세요 (ex.010-1234-5678)")
    @NotEmpty
    private String phoneNumber;

    @ApiModelProperty(value = "비밀번호", example = "test1234" , required = true)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상 문자와 숫자를 조합여주시기 바랍니다.")
    @NotEmpty
    private String password;
}
