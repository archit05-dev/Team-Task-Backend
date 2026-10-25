package com.finnit.backend.controller;

import com.finnit.backend.dto.SaveRepositoryRequest;
import com.finnit.backend.dto.SavedRepositoryResponse;
import com.finnit.backend.entity.User;
import com.finnit.backend.service.SavedRepositoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-repositories")
public class SavedRepositoryController {

    private final SavedRepositoryService savedRepositoryService;

    public SavedRepositoryController(
            SavedRepositoryService savedRepositoryService
    ) {
        this.savedRepositoryService = savedRepositoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRepositoryResponse saveRepository(
            @Valid @RequestBody SaveRepositoryRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return savedRepositoryService.saveRepository(user, request);
    }

    @GetMapping
    public List<SavedRepositoryResponse> getSavedRepositories(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return savedRepositoryService.getSavedRepositories(user);
    }

    @DeleteMapping("/{savedRepositoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSavedRepository(
            @PathVariable Long savedRepositoryId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        savedRepositoryService.deleteSavedRepository(
                user,
                savedRepositoryId
        );
    }
}