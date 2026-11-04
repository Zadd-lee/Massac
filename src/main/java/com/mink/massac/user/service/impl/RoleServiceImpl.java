package com.mink.massac.user.service.impl;

import com.mink.massac.common.exception.CustomException;
import com.mink.massac.common.entity.Status;
import com.mink.massac.user.dto.CreateRoleRequestDto;
import com.mink.massac.user.entity.Role;
import com.mink.massac.user.exception.RoleErrorCode;
import com.mink.massac.user.repository.RoleRepository;
import com.mink.massac.user.repository.UserRoleRepository;
import com.mink.massac.user.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Override
    public void create(CreateRoleRequestDto dto){
        if(roleRepository.existsByCode(dto.code())) {
            throw new CustomException(RoleErrorCode.DUPLICATE_CODE);
        }

        Role role = dto.toEntity();
        roleRepository.save(role);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public void delete(Long roleId) {
        if (roleId == null || roleId <= 0) {
            throw new CustomException(RoleErrorCode.INVALID_ROLE);
        }
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomException(RoleErrorCode.ROLE_NOT_FOUND));
        if (role.getStatus() == Status.DELETED) {
            return;
        }
        if (userRoleRepository.existsByRole_Id(roleId)) {
            throw new CustomException(RoleErrorCode.ROLE_IN_USE);
        }
        role.delete();
    }
}
