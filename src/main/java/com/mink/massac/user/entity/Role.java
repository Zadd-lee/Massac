package com.mink.massac.user.entity;

import com.mink.massac.common.entity.CreateAndUpdateDate;
import com.mink.massac.common.entity.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_role_code", columnNames = "code"))
@Getter
@NoArgsConstructor
public class Role extends CreateAndUpdateDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany
    private List<UserRole> userRoleList;

    public Role(String code, String name) {
        this.code = code;
        this.name = name;
        this.status = Status.ENABLE;
    }

    public void addUser(User user) {
        this.userRoleList.add(new UserRole(this, user));
    }
}
