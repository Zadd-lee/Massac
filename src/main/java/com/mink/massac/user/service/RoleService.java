package com.mink.massac.user.service;

import com.mink.massac.user.dto.CreateRoleRequestDto;

public interface RoleService {
    void create(CreateRoleRequestDto dto);

    void delete(Long roleId);
}
