package app.project.ably.user.dto;

import app.project.ably.core.config.CipherService;
import app.project.ably.user.entity.User;
import app.project.ably.user.enums.UserRoleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class UserDTO {

    @ApiModelProperty(value = "id", example = "1")
    private Long id;

    @ApiModelProperty(value = "계정", example = "test")
    private String loginId;

    @ApiModelProperty(value = "비밀번호", example = "test1234")
    private String password;

    @ApiModelProperty(value = "사용자 이름", example = "홍길동")
    private String name;

    @ApiModelProperty(value = "사용자 주민번호", example = "920910-1234567")
    private String regNo;

    @ApiModelProperty(value = "핸드폰 번호", example = "010-1234-5678")
    private String phoneNumber;

    @ApiModelProperty(value = "계정권한", example = "USER")
    private UserRoleType userRoleType;

    @ApiModelProperty(value = "생성날짜", example = "2022-04-07T00:55:00.181448")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "수정날짜", example = "2022-04-07T00:55:00.181448")
    private LocalDateTime updatedDate;

    public static UserDTO fromWithOutPassword(User user, CipherService cipherService) {
        return UserDTO.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .userRoleType(user.getUserRoleType())
                .regNo(cipherService.decrypt(user.getRegNo()))
                .phoneNumber(user.getPhoneNumber())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }

    public static User toEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .loginId(userDTO.getLoginId())
                .password(userDTO.getPassword())
                .name(userDTO.getName())
                .regNo(userDTO.getRegNo())
                .phoneNumber(userDTO.getPhoneNumber())
                .userRoleType(userDTO.getUserRoleType())
                .build();
    }
}
