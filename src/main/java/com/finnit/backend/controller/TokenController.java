package com.finnit.backend.controller;

import com.finnit.backend.dto.TokenRequest;
import com.finnit.backend.dto.TokenResponse;
import com.finnit.backend.entity.User;
import com.finnit.backend.service.JwtService;
import com.finnit.backend.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public TokenController(
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody TokenRequest request) {

        User user = refreshTokenService
                .rotateRefreshToken(request.refreshToken());

        String accessToken =
                jwtService.generateAccessToken(user.getId());

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenRequest request) {

        refreshTokenService.revokeRefreshToken(request.refreshToken());

        return ResponseEntity.noContent().build();
    }
}