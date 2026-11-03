package com.mink.massac.common.exception;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import tools.jackson.databind.json.JsonMapper;

/** MVC 예외 처리기가 처리하지 못하는 보안 필터 단계의 응답에 사용합니다. */
public final class ErrorResponseWriter {
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private ErrorResponseWriter() {}

    public static void write(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setStatus(code.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(MAPPER.writeValueAsString(ErrorResponse.of(code)));
    }
}
