
package com.finnit.backend.security;

import com.finnit.backend.entity.User;
import com.finnit.backend.service.AuthService;
import com.finnit.backend.service.JwtService;
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
    private final JwtService jwtService;

    public GitHubOAuthSuccessHandler(
            AuthService authService,
            JwtService jwtService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {


        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        User user = authService.processGitHubUser(oauth2User);

        String accessToken = jwtService.generateAccessToken(
                user.getId()
        );



        //Temporary until jwt is added
        response.sendRedirect("http://localhost:3000");
    }
}
