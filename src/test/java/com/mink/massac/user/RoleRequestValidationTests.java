package com.mink.massac.user;

import com.mink.massac.user.dto.CreateRoleRequestDto;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleRequestValidationTests {
    @Test
    void nameLengthMatchesDatabaseLimit() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new CreateRoleRequestDto("mng_003", "가".repeat(255))).isEmpty());
            var violations = validator.validate(new CreateRoleRequestDto("mng_003", "가".repeat(256)));
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }
    }

    @Test
    void malformedCodeAndBlankNameAreRejected() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator().validate(new CreateRoleRequestDto("mng_03", " "));
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }
    }
}
