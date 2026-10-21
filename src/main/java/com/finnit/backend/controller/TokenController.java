package com.finnit.backend.controller;

import com.finnit.backend.dto.OAuthCodeRequest;
import com.finnit.backend.dto.TokenRequest;
import com.finnit.backend.dto.TokenResponse;
import com.finnit.backend.entity.User;
import com.finnit.backend.service.JwtService;
import com.finnit.backend.service.OAuthCodeService;
import com.finnit.backend.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OAuthCodeService oauthCodeService;

    public TokenController(
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            OAuthCodeService oauthCodeService
    ) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.oauthCodeService = oauthCodeService;
    }

    @Transactional
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody TokenRequest request) {
        return refreshTokenService.rotateRefreshToken(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenRequest request) {

        refreshTokenService.revokeRefreshToken(request.refreshToken());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchange")
    public TokenResponse exchange(@RequestBody OAuthCodeRequest request) {

        User user = oauthCodeService.consumeCode(request.code());

        String accessToken = jwtService.generateAccessToken(user.getId());

        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken);
    }
}