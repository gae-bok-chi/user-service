package com.gaebokchi.userservice.entity;

import lombok.*;

import javax.persistence.*;


@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_token")
@Entity
public class OAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4096)
    private String accessToken;

    @Column(length = 4096)
    private String refreshToken;

    @Column(length = 4096)
    private String authentication;

    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private User user;

    public OAuthToken updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        return this;
    }
}
