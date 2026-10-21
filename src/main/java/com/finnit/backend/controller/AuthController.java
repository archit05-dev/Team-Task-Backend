
package com.finnit.backend.controller;

import com.finnit.backend.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/api/auth/me")
    public Map<String, Object> currentUser(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return Map.of(
                "id", user.getId(),
                "githubId", user.getGithubId(),
                "username", user.getGithubUsername(),
                "name", user.getName() == null ? "" : user.getName(),
                "avatarUrl", user.getAvatarUrl() == null ? "" : user.getAvatarUrl()
        );
    }
}
