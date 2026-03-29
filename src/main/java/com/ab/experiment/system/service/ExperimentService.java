package com.ab.experiment.system.service;

import com.ab.experiment.system.config.ExperimentConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExperimentService {

    private final ExperimentConfig config;

    public ExperimentService(ExperimentConfig config) {
        this.config = config;
    }

    public Map<String, String> getAllVariants(String userId) {

        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> experiment :
                config.getExperiments().entrySet()) {

            String experimentId = experiment.getKey();
            Map<String, Integer> variants = experiment.getValue();

            String variant = assignVariant(userId, experimentId, variants);

            result.put(experimentId, variant);
        }

        return result;
    }

    private String assignVariant(String userId,
                                 String experimentId,
                                 Map<String, Integer> variants) {

        String key = userId + ":" + experimentId;
        int hash = Math.abs(key.hashCode());

        int bucket = hash % 100;

        int cumulative = 0;

        for (Map.Entry<String, Integer> entry : variants.entrySet()) {
            cumulative += entry.getValue();

            if (bucket < cumulative) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Assignment failed");
    }
}