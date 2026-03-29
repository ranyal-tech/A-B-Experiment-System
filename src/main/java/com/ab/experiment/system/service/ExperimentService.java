package com.ab.experiment.system.service;

import com.ab.experiment.system.config.ExperimentConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExperimentService {

    private final ExperimentConfig config;
    private final StringRedisTemplate redisTemplate;

    public ExperimentService(ExperimentConfig config,
                             StringRedisTemplate redisTemplate) {
        this.config = config;
        this.redisTemplate = redisTemplate;
    }

    public Map<String, String> getAllVariants(String userId) {

        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> experiment :
                config.getExperiments().entrySet()) {

            String experimentId = experiment.getKey();

            String cacheKey = "exp:" + userId + ":" + experimentId;

            // ✅ 1. Check Redis
            String cachedVariant = redisTemplate.opsForValue().get(cacheKey);

            if (cachedVariant != null) {
                result.put(experimentId, cachedVariant);
                continue;
            }

            // ✅ 2. Compute if not cached
            String variant = assignVariant(
                    userId,
                    experimentId,
                    experiment.getValue()
            );

            // ✅ 3. Store in Redis
            redisTemplate.opsForValue().set(cacheKey, variant);

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