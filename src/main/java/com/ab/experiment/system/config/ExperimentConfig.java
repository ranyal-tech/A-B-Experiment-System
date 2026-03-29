package com.ab.experiment.system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;

@Component
public class ExperimentConfig {

    private Map<String, Map<String, Integer>> experiments;

    public Map<String, Map<String, Integer>> getExperiments() {
        return experiments;
    }

    @PostConstruct
    public void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("experiments.json");

            experiments = mapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
