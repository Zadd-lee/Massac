package com.mink.massac.user.controller;

import com.mink.massac.user.dto.CreateRoleRequestDto;
import com.mink.massac.user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Void> post(@Valid @RequestBody CreateRoleRequestDto dto) {
        roleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
