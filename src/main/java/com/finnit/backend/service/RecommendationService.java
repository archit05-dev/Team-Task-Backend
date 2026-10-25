package com.finnit.backend.service;

import com.finnit.backend.dto.OnboardingRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    private final GitHubRepositoryService gitHubRepositoryService;
    private final GitHubSearchService gitHubSearchService;
    private final MLProfileService mlProfileService;
    private final MLService mlService;

    public RecommendationService(
            GitHubRepositoryService gitHubRepositoryService,
            GitHubSearchService gitHubSearchService,
            MLProfileService mlProfileService,
            MLService mlService
    ) {
        this.gitHubRepositoryService = gitHubRepositoryService;
        this.gitHubSearchService = gitHubSearchService;
        this.mlProfileService = mlProfileService;
        this.mlService = mlService;
    }

    public Map<String, Object> getRecommendations(
            String principalName,
            OnboardingRequest request
    ) {
        // Fetch the user's public repositories
        List<Map<String, Object>> repositories =
                gitHubRepositoryService.getPublicRepositories(principalName);

        // Build the user's profile for the ML models
        Map<String, Object> profile =
                mlProfileService.buildProfile(request, repositories);

        // Model 1 generates recommendation tags
        List<String> tags = mlService.generateTags(profile);

        // Model 2 returns ranked repository names
        List<String> recommendedRepositories =
                mlService.getRecommendations(profile);

        // Resolve repository names into GitHub repository details
        List<Map<String, Object>> enrichedRecommendations =
                new ArrayList<>();

        for (int i = 0; i < recommendedRepositories.size(); i++) {

            String repositoryName = recommendedRepositories.get(i);

            Map<String, Object> repository =
                    gitHubSearchService.searchRepositoryByName(
                            principalName,
                            repositoryName
                    );

            if (repository == null) {
                continue;
            }

            Map<?, ?> owner =
                    repository.get("owner") instanceof Map<?, ?>
                            ? (Map<?, ?>) repository.get("owner")
                            : null;

            Map<String, Object> result = new LinkedHashMap<>();

            result.put("id", repository.get("id"));
            result.put("name", repository.get("name"));
            result.put("owner", owner != null ? owner.get("login") : null);
            result.put("fullName", repository.get("full_name"));
            result.put("description", repository.get("description"));
            result.put("stars", repository.get("stargazers_count"));
            result.put("url", repository.get("html_url"));
            result.put("topics", repository.get("topics"));

            result.put(
                    "difficulty",
                    getDifficulty(
                            request.getPreferredDifficulty(),
                            i
                    )
            );

            enrichedRecommendations.add(result);
        }

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("tags", tags);
        response.put("recommendations", enrichedRecommendations);
        response.put("preferredDifficulty", request.getPreferredDifficulty());

        return response;
    }

    private String getDifficulty(String preferredDifficulty, int index) {

        if (preferredDifficulty == null) {
            return "UNKNOWN";
        }

        switch (preferredDifficulty.toUpperCase()) {

            case "EASY":
                if (index < 4) {
                    return "EASY";
                } else if (index < 7) {
                    return "MEDIUM";
                } else {
                    return "HARD";
                }

            case "MEDIUM":
                if (index < 3) {
                    return "EASY";
                } else if (index < 7) {
                    return "MEDIUM";
                } else {
                    return "HARD";
                }

            case "HARD":
                if (index < 3) {
                    return "EASY";
                } else if (index < 6) {
                    return "MEDIUM";
                } else {
                    return "HARD";
                }

            default:
                return "UNKNOWN";
        }
    }
}