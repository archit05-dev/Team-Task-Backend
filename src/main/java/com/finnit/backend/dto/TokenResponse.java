package com.finnit.backend.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}