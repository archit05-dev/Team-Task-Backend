package com.finnit.backend.controller;

import com.finnit.backend.dto.OnboardingRequest;
import com.finnit.backend.entity.User;
import com.finnit.backend.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService
    ) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public Map<String, Object> getRecommendations(
            @Valid @RequestBody OnboardingRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return recommendationService.getRecommendations(
                user.getGithubId().toString(),
                request
        );
    }
}