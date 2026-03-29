package com.ab.experiment.system.service;

import com.ab.experiment.system.config.ExperimentConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExperimentServiceTest {

    private ExperimentService service;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setup() {
        ExperimentConfig config = new ExperimentConfig();
        config.load();

        // 🔥 Mock Redis
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service = new ExperimentService(config, redisTemplate);
    }

    // ✅ 1. Deterministic test
    @Test
    void shouldReturnSameResultForSameUser() {
        when(valueOperations.get(anyString())).thenReturn(null);

        Map<String, String> r1 = service.getAllVariants("user123");
        Map<String, String> r2 = service.getAllVariants("user123");

        assertEquals(r1, r2);
    }

    // ✅ 2. Ensure both experiments exist
    @Test
    void shouldReturnBothExperiments() {
        when(valueOperations.get(anyString())).thenReturn(null);

        Map<String, String> result = service.getAllVariants("user1");

        assertTrue(result.containsKey("exp1"));
        assertTrue(result.containsKey("exp2"));
    }

    // ✅ 3. Validate exp1 distribution (70/30)
    @Test
    void shouldFollow70_30DistributionForExp1() {

        when(valueOperations.get(anyString())).thenReturn(null);

        int countA = 0;
        int countB = 0;
        int total = 1000;

        for (int i = 0; i < total; i++) {
            Map<String, String> result =
                    service.getAllVariants("user" + i);

            if ("A".equals(result.get("exp1"))) countA++;
            else if ("B".equals(result.get("exp1"))) countB++;
        }

        assertTrue(countA > 650 && countA < 750);
        assertTrue(countB > 250 && countB < 350);
    }

    // ✅ 4. Validate exp2 distribution (50/50)
    @Test
    void shouldFollow50_50DistributionForExp2() {

        when(valueOperations.get(anyString())).thenReturn(null);

        int countA = 0;
        int countB = 0;
        int total = 1000;

        for (int i = 0; i < total; i++) {
            Map<String, String> result =
                    service.getAllVariants("user" + i);

            if ("A".equals(result.get("exp2"))) countA++;
            else if ("B".equals(result.get("exp2"))) countB++;
        }

        assertTrue(countA > 450 && countA < 550);
        assertTrue(countB > 450 && countB < 550);
    }

    // ✅ 5. Ensure no null values
    @Test
    void shouldNotReturnNullVariants() {
        when(valueOperations.get(anyString())).thenReturn(null);

        Map<String, String> result = service.getAllVariants("user1");

        for (String variant : result.values()) {
            assertNotNull(variant);
        }
    }

    // 🔥 6. Redis Cache HIT test (VERY IMPORTANT)
    @Test
    void shouldReturnFromCacheIfPresent() {

        // Simulate cache hit
        when(valueOperations.get(anyString())).thenReturn("A");

        Map<String, String> result = service.getAllVariants("user1");

        // All experiments should return cached value
        for (String variant : result.values()) {
            assertEquals("A", variant);
        }

        // Ensure no computation (optional behavior check)
        verify(valueOperations, atLeastOnce()).get(anyString());
    }

    // 🔥 7. Redis Cache MISS test
    @Test
    void shouldStoreInCacheWhenNotPresent() {

        when(valueOperations.get(anyString())).thenReturn(null);

        service.getAllVariants("user1");

        // Verify cache is written
        verify(valueOperations, atLeastOnce())
                .set(anyString(), anyString());
    }
}