
package com.finnit.backend.security;

import com.finnit.backend.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GitHubOAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    public GitHubOAuthSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {


        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();


        authService.processGitHubUser(oauth2User);

        //Temporary until jwt is added
        response.sendRedirect("http://localhost:3000");
    }
}
