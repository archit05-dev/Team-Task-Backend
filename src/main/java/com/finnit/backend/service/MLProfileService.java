package com.finnit.backend.service;

import com.finnit.backend.dto.OnboardingRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MLProfileService {

    public Map<String, Object> buildProfile(
            OnboardingRequest request,
            List<Map<String, Object>> repositories
    ) {
        Set<String> languages = new LinkedHashSet<>();
        Set<String> topics = new LinkedHashSet<>();
        List<String> descriptions = new ArrayList<>();

        for (Map<String, Object> repository : repositories) {

            Object language = repository.get("language");

            if (language instanceof String
                    && !((String) language).isBlank()) {
                languages.add((String) language);
            }

            Object repositoryTopics = repository.get("topics");

            if (repositoryTopics instanceof List<?>) {
                for (Object topic : (List<?>) repositoryTopics) {
                    if (topic instanceof String) {
                        topics.add((String) topic);
                    }
                }
            }

            Object description = repository.get("description");

            if (description instanceof String
                    && !((String) description).isBlank()) {
                descriptions.add((String) description);
            }
        }

        Map<String, Object> profile = new HashMap<>();

        profile.put("language", new ArrayList<>(languages));
        profile.put("topics", new ArrayList<>(topics));

        profile.put("description", String.join(" ", descriptions));

        profile.put("introduction", request.getIntroduction());
        profile.put("preferedTechstack", request.getPreferredTechStack());
        profile.put("learningGoal", new ArrayList<String>());
        profile.put("intrest", request.getInterests());
        profile.put("preferedIssueType", request.getPreferredIssueTypes());

        return profile;
    }
}