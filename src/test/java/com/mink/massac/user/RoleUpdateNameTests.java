package com.mink.massac.user;

import com.mink.massac.common.entity.Status;
import com.mink.massac.common.exception.*;
import com.mink.massac.user.controller.RoleController;
import com.mink.massac.user.dto.UpdateRoleNameRequestDto;
import com.mink.massac.user.entity.Role;
import com.mink.massac.user.exception.RoleErrorCode;
import com.mink.massac.user.repository.*;
import com.mink.massac.user.service.RoleService;
import com.mink.massac.user.service.impl.RoleServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoleUpdateNameTests {
    private final RoleRepository roles = mock(RoleRepository.class);
    private final RoleService service = new RoleServiceImpl(roles, mock(UserRoleRepository.class));
    private final UpdateRoleNameRequestDto request = new UpdateRoleNameRequestDto("새 이름");

    @Test
    void changesOnlyName() {
        Role role = new Role("mng_003", "이전 이름");
        when(roles.findById(1L)).thenReturn(Optional.of(role));
        service.updateName(1L, request);
        assertEquals("새 이름", role.getName());
        assertEquals("mng_003", role.getCode());
        assertEquals(Status.ENABLE, role.getStatus());
    }

    @Test
    void missingRoleIsNotFound() {
        var error = assertThrows(CustomException.class, () -> service.updateName(1L, request));
        assertEquals(RoleErrorCode.ROLE_NOT_FOUND, error.getErrorCode());
    }

    @Test
    void deletedRoleCannotBeRenamed() {
        Role role = new Role("mng_003", "이전 이름");
        role.delete();
        when(roles.findById(1L)).thenReturn(Optional.of(role));
        var error = assertThrows(CustomException.class, () -> service.updateName(1L, request));
        assertEquals(RoleErrorCode.ROLE_NOT_FOUND, error.getErrorCode());
        assertEquals("이전 이름", role.getName());
    }

    @Test
    void invalidIdIsRejectedBeforeQuery() {
        var error = assertThrows(CustomException.class, () -> service.updateName(0L, request));
        assertEquals(RoleErrorCode.INVALID_ROLE, error.getErrorCode());
        verifyNoInteractions(roles);
    }

    @Test
    void endpointAcceptsNameAndRejectsInvalidInput() throws Exception {
        RoleService mockService = mock(RoleService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new RoleController(mockService))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(patch("/role/1/name").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"새 이름\"}"))
                .andExpect(status().isNoContent());
        verify(mockService).updateName(1L, request);
        clearInvocations(mockService);
        for (String body : List.of("{}", "{\"name\":\" \"}",
                "{\"name\":\"" + "가".repeat(256) + "\"}")) {
            mvc.perform(patch("/role/1/name").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(mockService);
    }

    @Test
    void methodSecurityRequiresUpdatePermission() {
        try (var context = new AnnotationConfigApplicationContext(SecurityConfig.class)) {
            var repo = context.getBean(RoleRepository.class);
            var secured = context.getBean(RoleService.class);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("operator", null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))));
            assertThrows(AccessDeniedException.class, () -> secured.updateName(1L, request));
            verifyNoInteractions(repo);
            when(repo.findById(1L)).thenReturn(Optional.of(new Role("mng_003", "이전 이름")));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("operator", null,
                            List.of(new SimpleGrantedAuthority("ROLE_UPDATE"))));
            assertDoesNotThrow(() -> secured.updateName(1L, request));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableMethodSecurity
    static class SecurityConfig {
        @Bean RoleRepository roles() { return mock(RoleRepository.class); }
        @Bean RoleService service(RoleRepository roles) {
            return new RoleServiceImpl(roles, mock(UserRoleRepository.class));
        }
    }
}
