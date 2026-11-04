package com.mink.massac.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRoleNameRequestDto(
        @NotBlank(message = "역할 이름은 필수입니다.")
        @Size(max = 255, message = "역할 이름은 255자 이하여야 합니다.")
        String name) {
}
