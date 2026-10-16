package com.mink.massac.user.entity;

import com.mink.massac.common.entity.CreateAndUpdateDate;
import com.mink.massac.common.entity.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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

    @EnumeratedValue
    @Column(nullable = false)
    private Status status;

    public Role(String code, String name, Status status) {
        this.code = code;
        this.name = name;
        this.status = status;
    }
}
