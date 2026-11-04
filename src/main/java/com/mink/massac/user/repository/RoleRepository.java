package com.mink.massac.user.repository;


import com.mink.massac.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByCode(String code);
}
