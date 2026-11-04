package com.mink.massac.user.entity;

import com.mink.massac.common.entity.CreateAndUpdateDate;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Table(name = "user_role")
public class UserRole extends CreateAndUpdateDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserRole(Role role, User user) {
        this.role = role;
        this.user = user;
    }


}
