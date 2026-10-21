
package com.finnit.backend.service;

import com.finnit.backend.entity.OAuthCode;
import com.finnit.backend.entity.User;
import com.finnit.backend.repository.OAuthCodeRepository;
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
public class OAuthCodeService {

    private final OAuthCodeRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    public OAuthCodeService(OAuthCodeRepository repository) {
        this.repository = repository;
    }

    private String generateCode() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashCode(String code) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(code.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    @Transactional
    public String createCode(User user) {
        String rawCode = generateCode();

        OAuthCode oauthCode = new OAuthCode(
                user,
                hashCode(rawCode),
                Instant.now().plusSeconds(60)
        );

        repository.save(oauthCode);

        return rawCode;
    }

    @Transactional
    public User consumeCode(String rawCode) {
        OAuthCode oauthCode = repository
                .findByCodeHashForUpdate(hashCode(rawCode))
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid authorization code"));

        if (oauthCode.isUsed()
                || !oauthCode.getExpiresAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException(
                    "Authorization code expired or already used");
        }

        oauthCode.setUsed(true);

        return oauthCode.getUser();
    }
}
