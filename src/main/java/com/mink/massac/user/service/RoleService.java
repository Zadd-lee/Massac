package com.mink.massac.user.service;

import com.mink.massac.user.dto.CreateRoleRequestDto;
import com.mink.massac.user.dto.UpdateRoleNameRequestDto;

public interface RoleService {
    void create(CreateRoleRequestDto dto);

    void delete(Long roleId);

    void updateName(Long roleId, UpdateRoleNameRequestDto dto);
}
