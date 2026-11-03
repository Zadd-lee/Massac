package com.mink.massac.user.service;

import com.mink.massac.common.exception.CustomException;
import com.mink.massac.user.dto.CreateRoleRequestDto;

public interface RoleService {
    void create(CreateRoleRequestDto dto) throws CustomException;
}
