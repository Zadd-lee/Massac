package com.mink.massac.common.exception;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> custom(CustomException e) {
        return response(e.getErrorCode());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (e instanceof MethodArgumentNotValidException validation) {
            var errors = validation.getBindingResult().getFieldErrors().stream()
                    .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                    .toList();
            return new ResponseEntity<>(new ErrorResponse("COMMON_400",
                    CommonErrorCode.INVALID_REQUEST.getMessage(), errors), headers, status);
        }
        // 프레임워크의 404/405/415 등 상태와 Allow 등의 헤더를 보존합니다.
        return new ResponseEntity<>(new ErrorResponse("HTTP_" + status.value(),
                status.is5xxServerError() ? CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                        : "요청을 처리할 수 없습니다.", List.of()), headers, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> conflict(DataIntegrityViolationException e) {
        return response(CommonErrorCode.CONFLICT);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> unavailable(DataAccessException e) {
        log.error("Data access failure", e);
        return response(CommonErrorCode.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> forbidden(AccessDeniedException e) {
        return response(CommonErrorCode.ACCESS_DENIED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> unauthorized(AuthenticationException e) {
        return response(CommonErrorCode.AUTHENTICATION_REQUIRED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> unexpected(Exception e) {
        log.error("Unexpected request failure", e);
        return response(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> response(ErrorCode code) {
        return ResponseEntity.status(code.getStatus()).body(ErrorResponse.of(code));
    }
}
