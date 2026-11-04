package com.mink.massac.user.repository;

import com.mink.massac.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByRole_Id(Long roleId);
}
