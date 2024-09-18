package com.msauth.repository;

import com.msauth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);

    @Query("""
            select t from Token t
            where t.user.id = :userId and t.loggedOut = false
            """)
    List<Token> findAllAccessTokensByUser(Long userId);
}