package com.finnit.backend.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GitHubSearchService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestClient gitHubRestClient;

    public GitHubSearchService(
            OAuth2AuthorizedClientService authorizedClientService,
            RestClient gitHubRestClient
    ) {
        this.authorizedClientService = authorizedClientService;
        this.gitHubRestClient = gitHubRestClient;
    }

    public Map<String, Object> searchRepositoryByName(
            String principalName,
            String repositoryName
    ) {

        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                        "github",
                        principalName
                );

        if (client == null) {
            throw new IllegalStateException(
                    "GitHub authorized client not found. Please log in again."
            );
        }

        String accessToken =
                client.getAccessToken().getTokenValue();

        RestClient.RequestHeadersUriSpec<?> requestBuilder =
                gitHubRestClient.get();

        RestClient.RequestHeadersSpec<?> request =
                requestBuilder.uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", repositoryName + " in:name")
                        .queryParam("per_page", 100)
                        .build());

        request.header("Authorization", "Bearer " + accessToken);

        Map<String, Object> response =
                request.retrieve()
                        .body(new ParameterizedTypeReference<
                                Map<String, Object>>() {});

        if (response == null) {
            return null;
        }

        Object itemsObject = response.get("items");

        if (!(itemsObject instanceof List<?> items)) {
            return null;
        }

        for (Object item : items) {
            if (!(item instanceof Map<?, ?> repository)) {
                continue;
            }

            Object name = repository.get("name");

            if (name instanceof String
                    && ((String) name).equalsIgnoreCase(repositoryName)) {

                @SuppressWarnings("unchecked")
                Map<String, Object> result =
                        (Map<String, Object>) repository;

                return result;
            }
        }

        return null;
    }
}