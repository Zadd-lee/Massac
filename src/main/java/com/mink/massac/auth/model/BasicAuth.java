package com.mink.massac.auth.model;

import com.mink.massac.common.entity.CreateAndUpdateDate;
import com.mink.massac.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "basic_auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicAuth extends CreateAndUpdateDate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    @Column(nullable = false, length = 100)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public BasicAuth(User user, String username, String encodedPassword) {
        this.user = Objects.requireNonNull(user, "user 객체가 필요합니다.");
        this.username = username;
        this.password = encodedPassword;
    }
}
