package com.gaebokchi.userservice.entity;

import com.gaebokchi.userservice.vo.Role;
import lombok.*;

import javax.persistence.*;


@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_table")
@Entity
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String name;

    @Column(length = 20)
    private String password;

    @Column(nullable = false, length = 50)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User updateModifiedDate(String name, String picture) {
        this.onPreUpdate();
        this.name = name;
        this.picture = picture;
        return this;
    }
}
