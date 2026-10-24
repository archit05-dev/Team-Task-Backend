package com.finnit.backend.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class MLService {

    private final RestClient mlRestClient;

    public MLService() {
        this.mlRestClient = RestClient.builder()
                .baseUrl("https://gdg-group-task-ml.onrender.com")
                .build();
    }

    public List<String> generateTags(Map<String, Object> userProfile) {
        return callModel("/tags", userProfile);
    }

    public List<String> getRecommendations(Map<String, Object> userProfile) {
        return callModel("/repo", userProfile);
    }

    private List<String> callModel(
            String endpoint,
            Map<String, Object> userProfile
    ) {
        Map<String, Object> response = mlRestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userProfile)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        System.out.println("ML endpoint: " + endpoint);
        System.out.println("ML response: " + response);

        if (response == null) {
            throw new IllegalStateException(
                    "Empty response from ML endpoint: " + endpoint
            );
        }

        Object result;

        if (endpoint.equals("/tags")) {
            result = response.get("Tags");
        }
        else if (endpoint.equals("/repo")) {
            result = response.get("Repo");
        }
        else {
            throw new IllegalArgumentException(
                    "Unknown ML endpoint: " + endpoint
            );
        }

        if (!(result instanceof List<?>)) {
            throw new IllegalStateException(
                    "Invalid response from ML endpoint: " + endpoint
                            + ". Expected a list, but received: " + response
            );
        }

        List<?> items = (List<?>) result;

        for (Object item : items) {
            if (!(item instanceof String)) {
                throw new IllegalStateException(
                        "Invalid item in ML response from endpoint: " + endpoint
                );
            }
        }

        @SuppressWarnings("unchecked")
        List<String> resultList = (List<String>) items;

        return resultList;
    }
}