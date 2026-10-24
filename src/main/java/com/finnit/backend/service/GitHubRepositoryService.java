
package com.finnit.backend.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GitHubRepositoryService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestClient gitHubRestClient;

    public GitHubRepositoryService(
            OAuth2AuthorizedClientService authorizedClientService,
            RestClient gitHubRestClient
    ) {
        this.authorizedClientService = authorizedClientService;
        this.gitHubRestClient = gitHubRestClient;
    }

    public List<Map<String, Object>> getPublicRepositories(
            String principalName
    ) {

        // Load the GitHub OAuth client
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

        // Get the access token
        String accessToken =
                client.getAccessToken().getTokenValue();

        // Fetch the user's public repositories
        // Build the request first
        RestClient.RequestHeadersUriSpec<?> requestBuilder =
                gitHubRestClient.get();

// Add the URL and authorization header separately
        RestClient.RequestHeadersSpec<?> request =
                requestBuilder
                        .uri("/user/repos?visibility=public&affiliation=owner&per_page=100");

        request.header("Authorization", "Bearer " + accessToken);
// Retrieve the response
        return request
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<Map<String, Object>>>() {});
    }
}
