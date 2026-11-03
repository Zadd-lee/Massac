package com.mink.massac.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTests {
    private final org.springframework.test.web.servlet.MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler()).build();

    @Test
    void customExceptionReturnsDefinedStatusAndCode() throws Exception {
        mvc.perform(get("/custom"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("COMMON_409"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void unexpectedExceptionDoesNotExposeInternalDetails() throws Exception {
        mvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("COMMON_500"))
                .andExpect(jsonPath("$.message").value(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @Test
    void unsupportedMethodKeepsFrameworkStatusAndHeaders() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/custom"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().exists("Allow"))
                .andExpect(jsonPath("$.code").value("HTTP_405"));
    }

    @RestController
    static class TestController {
        @GetMapping("/custom")
        public void custom() { throw new CustomException(CommonErrorCode.CONFLICT); }

        @GetMapping("/unexpected")
        public void unexpected() { throw new IllegalStateException("internal database details"); }
    }
}
