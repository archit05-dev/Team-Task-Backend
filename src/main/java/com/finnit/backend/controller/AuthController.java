
package com.finnit.backend.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/api/auth/me")
    public Map<String, Object> currentUser(Authentication authentication) {


        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();


        return Map.of(
                "githubId", oauth2User.getAttribute("id"),
                "username", oauth2User.getAttribute("login"),
                "name", oauth2User.getAttribute("name"),
                "avatarUrl", oauth2User.getAttribute("avatar_url")
        );
    }
}
