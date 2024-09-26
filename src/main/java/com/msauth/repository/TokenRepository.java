package com.msauth.repository;

import com.msauth.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {

    Optional<TokenEntity> findByAccessToken(String accessToken);

    Optional<TokenEntity> findByRefreshToken(String refreshToken);

    @Query("""
            select t from TokenEntity t
            where t.user.id = :userId and t.loggedOut = false
            """)
    List<TokenEntity> findAllAccessTokensByUser(Long userId);
}