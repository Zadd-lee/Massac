package com.mink.massac.user;

import com.mink.massac.common.exception.CustomException;
import com.mink.massac.user.dto.CreateRoleRequestDto;
import com.mink.massac.user.entity.Role;
import com.mink.massac.user.exception.RoleErrorCode;
import com.mink.massac.user.repository.RoleRepository;
import com.mink.massac.user.service.RoleService;
import com.mink.massac.user.service.impl.RoleServiceImpl;
import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleCreateTests {
    private final RoleRepository repository = mock(RoleRepository.class);
    private final RoleService service = new RoleServiceImpl(repository);
    private final CreateRoleRequestDto request = new CreateRoleRequestDto("mng_003", "관리자");

    @AfterEach
    void clear() { SecurityContextHolder.clearContext(); }

    @Test
    void normalCreationSavesRole() {
        service.create(request);
        verify(repository).save(any(Role.class));
    }

    @Test
    void existingCodeIsRejectedBeforeInsert() {
        when(repository.existsByCode(request.code())).thenReturn(true);
        assertEquals(RoleErrorCode.DUPLICATE_CODE,
                assertThrows(CustomException.class, () -> service.create(request)).getErrorCode());
        verify(repository, never()).save(any());
    }

    @Test
    void duplicateAtInsertIsPropagatedToGlobalHandler() {
        var failure = new DataIntegrityViolationException("duplicate",
                new ConstraintViolationException("duplicate", new SQLException(), "role.uk_role_code"));
        when(repository.save(any())).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class, () -> service.create(request)));
    }

    @Test
    void unrelatedConstraintIsNotReportedAsDuplicateCode() {
        var failure = new DataIntegrityViolationException("other",
                new ConstraintViolationException("other", new SQLException(), "other_constraint"));
        when(repository.save(any())).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class, () -> service.create(request)));
    }

    @Test
    void methodSecurityRejectsUserAndAllowsCreatePermission() {
        try (var context = new AnnotationConfigApplicationContext(SecurityTestConfig.class)) {
            RoleService secured = context.getBean(RoleService.class);
            RoleRepository repo = context.getBean(RoleRepository.class);
            authenticate("ROLE_USER");
            assertThrows(AccessDeniedException.class, () -> secured.create(request));
            verifyNoInteractions(repo);
            authenticate("ROLE_CREATE");
            secured.create(request);
            verify(repo).save(any(Role.class));
        }
    }

    private void authenticate(String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("operator", null,
                        List.of(new SimpleGrantedAuthority(authority))));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableMethodSecurity
    static class SecurityTestConfig {
        @Bean RoleRepository repository() { return mock(RoleRepository.class); }
        @Bean RoleService service(RoleRepository repository) { return new RoleServiceImpl(repository); }
    }
}
