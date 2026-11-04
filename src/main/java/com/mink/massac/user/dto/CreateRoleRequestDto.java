package com.mink.massac.user.dto;

import com.mink.massac.user.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;


/**
 * DTO for {@link Role}
 */

public record CreateRoleRequestDto(@NotBlank(message = "필수값이 누락되었습니다")
                                   @Pattern(
                                           regexp = "^[a-z]{3,4}_[0-9]{3}$",
                                           message = "역할 코드는 영문 소문자 3~4자리_숫자 3자리 형식이어야 합니다."
                                   ) String code,
                                   @NotBlank(message = "필수값이 누락되었습니다.")
                                   @Size(max = 255, message = "역할 이름은 255자 이하여야 합니다.") String name) {
    public Role toEntity() {
        return new Role(code,name);
    }
}
