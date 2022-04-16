package app.project.ably.user.dto;

import app.project.ably.common.validation.annotation.RegNoValid;
import app.project.ably.user.entity.User;
import app.project.ably.user.enums.UserRoleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class UserRegDTO {

    @Autowired


    @ApiModelProperty(value = "계정", example = "test", required = true)
    @NotEmpty
    @Size(min = 3, max = 30, message = "사용자 아이디는 {min} ~ {max} 사이여야 합니다.")
    private String loginId;

    @ApiModelProperty(value = "비밀번호", example = "test1234" , required = true)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "{exception.validate.password}")
    @NotEmpty
    private String password;

    @ApiModelProperty(value = "사용자 이름", example = "홍길동", required = true)
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "사용자 주민번호", example = "920910-1234567", required = true)
    @RegNoValid
    private String regNo;

    @ApiModelProperty(value = "핸드폰 번호", example = "010-1234-5678", required = true)
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "{exception.validate.phoneNumber}")
    @NotEmpty
    private String phoneNumber;

    @ApiModelProperty(value = "사용자 권한", hidden = true)
    private UserRoleType userRoleType = UserRoleType.USER;

    public static User toEntity(UserRegDTO userRegDTO) {
        return User.builder()
                .loginId(userRegDTO.getLoginId())
                .password(userRegDTO.getPassword())
                .name(userRegDTO.getName())
                .regNo(userRegDTO.getRegNo())
                .phoneNumber(userRegDTO.getPhoneNumber())
                .userRoleType(userRegDTO.getUserRoleType())
                .build();
    }
}
