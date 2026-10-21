
package com.finnit.backend.security;

import com.finnit.backend.entity.User;
import com.finnit.backend.service.AuthService;
import com.finnit.backend.service.JwtService;
import com.finnit.backend.service.OAuthCodeService;
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
    private final OAuthCodeService oauthCodeService;

    public GitHubOAuthSuccessHandler(
            AuthService authService,
            JwtService jwtService,
            OAuthCodeService oauthCodeService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.oauthCodeService = oauthCodeService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {


        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        User user = authService.processGitHubUser(oauth2User);

        String code = oauthCodeService.createCode(user);

        response.sendRedirect(
                "http://localhost:3000?code=" +
                        java.net.URLEncoder.encode(
                                code,
                                java.nio.charset.StandardCharsets.UTF_8
                        )
        );
    }
}
