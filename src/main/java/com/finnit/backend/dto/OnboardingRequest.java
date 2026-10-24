package com.finnit.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OnboardingRequest {

    @Size(max = 300, message = "Introduction cannot exceed 300 characters")
    private String introduction;

    @NotNull
    private List<String> preferredTechStack;

    @NotNull
    private List<String> interests;

    @NotNull
    private List<String> preferredIssueTypes;

    @NotNull
    private String preferredDifficulty;

    public OnboardingRequest() {
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<String> getPreferredTechStack() {
        return preferredTechStack;
    }

    public void setPreferredTechStack(List<String> preferredTechStack) {
        this.preferredTechStack = preferredTechStack;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getPreferredIssueTypes() {
        return preferredIssueTypes;
    }

    public void setPreferredIssueTypes(List<String> preferredIssueTypes) {
        this.preferredIssueTypes = preferredIssueTypes;
    }

    public String getPreferredDifficulty() {
        return preferredDifficulty;
    }

    public void setPreferredDifficulty(String preferredDifficulty) {
        this.preferredDifficulty = preferredDifficulty;
    }
}