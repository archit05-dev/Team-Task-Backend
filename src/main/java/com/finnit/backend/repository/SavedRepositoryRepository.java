package com.finnit.backend.repository;

import com.finnit.backend.entity.SavedRepository;
import com.finnit.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedRepositoryRepository
        extends JpaRepository<SavedRepository, Long> {

    List<SavedRepository> findByUserOrderBySavedAtDesc(User user);

    boolean existsByUserAndGithubRepositoryId(
            User user,
            Long githubRepositoryId
    );

    Optional<SavedRepository> findByIdAndUser(
            Long id,
            User user
    );
}