package com.finnit.backend.dto;

import com.finnit.backend.entity.SavedRepository;

import java.time.LocalDateTime;

public record SavedRepositoryResponse(
        Long id,
        Long githubRepositoryId,
        String repositoryName,
        String owner,
        String repositoryUrl,
        String description,
        LocalDateTime savedAt
) {

    public static SavedRepositoryResponse from(
            SavedRepository savedRepository
    ) {
        return new SavedRepositoryResponse(
                savedRepository.getId(),
                savedRepository.getGithubRepositoryId(),
                savedRepository.getRepositoryName(),
                savedRepository.getOwner(),
                savedRepository.getRepositoryUrl(),
                savedRepository.getDescription(),
                savedRepository.getSavedAt()
        );
    }
}