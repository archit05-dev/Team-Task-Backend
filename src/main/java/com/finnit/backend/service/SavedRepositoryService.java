package com.finnit.backend.service;

import com.finnit.backend.dto.SaveRepositoryRequest;
import com.finnit.backend.dto.SavedRepositoryResponse;
import com.finnit.backend.entity.SavedRepository;
import com.finnit.backend.entity.User;
import com.finnit.backend.repository.SavedRepositoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SavedRepositoryService {

    private final SavedRepositoryRepository savedRepositoryRepository;

    public SavedRepositoryService(
            SavedRepositoryRepository savedRepositoryRepository
    ) {
        this.savedRepositoryRepository = savedRepositoryRepository;
    }

    @Transactional
    public SavedRepositoryResponse saveRepository(
            User user,
            SaveRepositoryRequest request
    ) {

        if (savedRepositoryRepository.existsByUserAndGithubRepositoryId(
                user,
                request.githubRepositoryId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Repository is already saved"
            );
        }

        SavedRepository savedRepository = new SavedRepository();

        savedRepository.setUser(user);
        savedRepository.setGithubRepositoryId(
                request.githubRepositoryId()
        );
        savedRepository.setRepositoryName(
                request.repositoryName()
        );
        savedRepository.setOwner(
                request.owner()
        );
        savedRepository.setRepositoryUrl(
                request.repositoryUrl()
        );
        savedRepository.setDescription(
                request.description()
        );

        SavedRepository saved =
                savedRepositoryRepository.save(savedRepository);

        return SavedRepositoryResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<SavedRepositoryResponse> getSavedRepositories(User user) {
        return savedRepositoryRepository
                .findByUserOrderBySavedAtDesc(user)
                .stream()
                .map(SavedRepositoryResponse::from)
                .toList();
    }

    @Transactional
    public void deleteSavedRepository(
            User user,
            Long savedRepositoryId
    ) {
        SavedRepository savedRepository =
                savedRepositoryRepository
                        .findByIdAndUser(savedRepositoryId, user)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Saved repository not found"
                        ));

        savedRepositoryRepository.delete(savedRepository);
    }
}