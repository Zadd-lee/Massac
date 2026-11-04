package com.mink.massac.user.service;

import com.mink.massac.common.entity.Status;
import com.mink.massac.common.exception.CustomException;
import com.mink.massac.user.entity.Role;
import com.mink.massac.user.exception.RoleErrorCode;
import com.mink.massac.user.repository.RoleRepository;
import com.mink.massac.user.repository.UserRoleRepository;
import com.mink.massac.user.service.impl.RoleServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceDeleteTests {
    private final RoleRepository roles = mock(RoleRepository.class);
    private final UserRoleRepository assignments = mock(UserRoleRepository.class);
    private final RoleService service = new RoleServiceImpl(roles, assignments);

    @Test
    void unassignedRoleIsSoftDeleted() {
        Role role = new Role("mng_003", "관리자");
        when(roles.findById(1L)).thenReturn(Optional.of(role));

        service.delete(1L);

        assertEquals(Status.DELETED, role.getStatus());
        verify(roles, never()).delete(any(Role.class));
        verify(roles, never()).deleteById(anyLong());
    }

    @Test
    void assignedRoleCannotBeDeleted() {
        Role role = new Role("mng_003", "관리자");
        when(roles.findById(1L)).thenReturn(Optional.of(role));
        when(assignments.existsByRole_Id(1L)).thenReturn(true);

        var error = assertThrows(CustomException.class, () -> service.delete(1L));

        assertEquals(RoleErrorCode.ROLE_IN_USE, error.getErrorCode());
        assertEquals(Status.ENABLE, role.getStatus());
    }

    @Test
    void missingRoleReturnsNotFound() {
        when(roles.findById(1L)).thenReturn(Optional.empty());

        var error = assertThrows(CustomException.class, () -> service.delete(1L));

        assertEquals(RoleErrorCode.ROLE_NOT_FOUND, error.getErrorCode());
        verifyNoInteractions(assignments);
    }

    @Test
    void repeatedDeletionSucceeds() {
        Role role = new Role("mng_003", "관리자");
        role.delete();
        when(roles.findById(1L)).thenReturn(Optional.of(role));

        assertDoesNotThrow(() -> service.delete(1L));

        verifyNoInteractions(assignments);
    }

    @Test
    void invalidIdIsRejectedBeforeQuery() {
        var error = assertThrows(CustomException.class, () -> service.delete(0L));

        assertEquals(RoleErrorCode.INVALID_ROLE, error.getErrorCode());
        verifyNoInteractions(roles, assignments);
    }
}
