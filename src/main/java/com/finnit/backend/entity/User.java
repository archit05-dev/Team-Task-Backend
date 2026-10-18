
package com.finnit.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_github_id",
                        columnNames = "github_id"
                )
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "github_id", nullable = false, unique = true)
    private Long githubId;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    private String name;

    private String avatarUrl;

    private String email;

    public User() {
    }

    public User(Long githubId, String githubUsername,
                String name, String avatarUrl, String email) {
        this.githubId = githubId;
        this.githubUsername = githubUsername;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public Long getGithubId() {
        return githubId;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getEmail() {
        return email;
    }


    public void setGithubId(Long githubId) {
        this.githubId = githubId;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
