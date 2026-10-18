
package com.finnit.backend.service;

import com.finnit.backend.entity.User;
import com.finnit.backend.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User processGitHubUser(OAuth2User oauth2User) {


        Long githubId = ((Number) oauth2User.getAttribute("id")).longValue();
        String username = oauth2User.getAttribute("login");
        String name = oauth2User.getAttribute("name");
        String avatarUrl = oauth2User.getAttribute("avatar_url");
        String email = oauth2User.getAttribute("email");


        User user = userRepository.findByGithubId(githubId)
                .orElseGet(() -> new User(
                        githubId, username, name, avatarUrl, email
                ));


        user.setGithubUsername(username);
        user.setName(name);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);


        return userRepository.save(user);
    }
}
