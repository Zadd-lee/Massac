package com.mink.massac.common.exception;

import org.springframework.http.HttpStatus;

/** 도메인별 오류 Enum도 이 계약을 구현할 수 있습니다. */
public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
