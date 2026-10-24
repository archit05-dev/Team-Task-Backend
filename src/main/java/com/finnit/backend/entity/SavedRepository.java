package com.finnit.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_repositories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_github_repository",
                        columnNames = {"user_id", "github_repository_id"}
                )
        }
)
public class SavedRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "github_repository_id", nullable = false)
    private Long githubRepositoryId;

    @Column(nullable = false)
    private String repositoryName;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, length = 1000)
    private String repositoryUrl;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime savedAt;

    public SavedRepository() {
    }

    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getGithubRepositoryId() {
        return githubRepositoryId;
    }

    public void setGithubRepositoryId(Long githubRepositoryId) {
        this.githubRepositoryId = githubRepositoryId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }
}