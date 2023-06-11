package com.gaebokchi.userservice.repository;

import com.gaebokchi.userservice.entity.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<OAuthToken, Long> {

    Optional<OAuthToken> findByUserId(Long userId);
}
