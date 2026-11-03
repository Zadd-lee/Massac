package com.mink.massac.user.service.impl;

import com.mink.massac.common.exception.CustomException;
import com.mink.massac.user.dto.CreateRoleRequestDto;
import com.mink.massac.user.entity.Role;
import com.mink.massac.user.exception.RoleErrorCode;
import com.mink.massac.user.repository.RoleRepository;
import com.mink.massac.user.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

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
}
