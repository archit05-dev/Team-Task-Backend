package com.finnit.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaveRepositoryRequest(

        @NotNull
        @Positive
        Long githubRepositoryId,

        @NotBlank
        String repositoryName,

        @NotBlank
        String owner,

        @NotBlank
        String repositoryUrl,

        String description

) {
}