package com.finnit.backend.service;

import com.finnit.backend.dto.OnboardingRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    private final GitHubRepositoryService gitHubRepositoryService;
    private final MLProfileService mlProfileService;
    private final MLService mlService;

    public RecommendationService(
            GitHubRepositoryService gitHubRepositoryService,
            MLProfileService mlProfileService,
            MLService mlService
    ) {
        this.gitHubRepositoryService = gitHubRepositoryService;
        this.mlProfileService = mlProfileService;
        this.mlService = mlService;
    }

    public Map<String, Object> getRecommendations(
            String principalName,
            OnboardingRequest request
    ) {

        List<Map<String, Object>> repositories =
                gitHubRepositoryService.getPublicRepositories(principalName);

        Map<String, Object> profile =
                mlProfileService.buildProfile(request, repositories);

        List<String> tags = mlService.generateTags(profile);

        List<String> recommendedRepositories =
                mlService.getRecommendations(profile);

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("tags", tags);
        response.put("recommendations", recommendedRepositories);
        response.put("preferredDifficulty", request.getPreferredDifficulty());

        return response;
    }
}