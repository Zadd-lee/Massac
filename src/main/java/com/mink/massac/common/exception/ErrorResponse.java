package com.mink.massac.common.exception;

import java.util.List;

public record ErrorResponse(String code, String message, List<FieldError> errors) {
    public ErrorResponse {
        errors = List.copyOf(errors);
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), List.of());
    }

    // 비밀번호 등의 입력 원문(rejectedValue)은 응답에 포함하지 않습니다.
    public record FieldError(String field, String message) {}
}
