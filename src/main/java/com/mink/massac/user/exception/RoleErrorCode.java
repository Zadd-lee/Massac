package com.mink.massac.user.exception;

import com.mink.massac.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RoleErrorCode implements ErrorCode {
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "ROLE_400", "요청 정보를 확인해 주세요."),
    DUPLICATE_CODE(HttpStatus.CONFLICT, "ROLE_409", "중복된 코드는 사용할 수 없습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_404", "역할을 찾을 수 없습니다."),
    ROLE_IN_USE(HttpStatus.CONFLICT, "ROLE_IN_USE", "사용자에게 배정된 역할은 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    RoleErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
