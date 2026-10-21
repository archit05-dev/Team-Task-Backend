package com.finnit.backend.service;

import com.finnit.backend.dto.TokenResponse;
import com.finnit.backend.entity.RefreshToken;
import com.finnit.backend.entity.User;
import com.finnit.backend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    @Transactional
    public String createRefreshToken(User user) {
        return saveNewRefreshToken(user);
    }

    @Transactional
    public TokenResponse rotateRefreshToken(String rawToken) {

        RefreshToken storedToken = repository
                .findByTokenHashForUpdate(hashToken(rawToken))
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid refresh token"));

        if (storedToken.isRevoked()
                || !storedToken.getExpiresAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException(
                    "Expired or revoked refresh token");
        }

        User user = storedToken.getUser();

        storedToken.setRevoked(true);

        String newRefreshToken = saveNewRefreshToken(user);

        String newAccessToken = jwtService.generateAccessToken(user.getId());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        repository.findByTokenHash(hashToken(rawToken))
                .ifPresent(token -> token.setRevoked(true));
    }

    private String saveNewRefreshToken(User user) {
        String rawToken = generateRandomToken();

        RefreshToken refreshToken = new RefreshToken(
                user,
                hashToken(rawToken),
                Instant.now().plusMillis(refreshExpiration)
        );

        repository.save(refreshToken);

        return rawToken;
    }


}