package com.ab.experiment.system.service;

import com.ab.experiment.system.config.ExperimentConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExperimentServiceTest {

    private ExperimentService service;

    @BeforeEach
    void setup() {
        ExperimentConfig config = new ExperimentConfig();
        config.load();
        service = new ExperimentService(config);
    }

    // ✅ 1. Deterministic test
    @Test
    void shouldReturnSameResultForSameUser() {
        Map<String, String> r1 = service.getAllVariants("user123");
        Map<String, String> r2 = service.getAllVariants("user123");

        assertEquals(r1, r2);
    }

    // ✅ 2. Ensure both experiments exist
    @Test
    void shouldReturnBothExperiments() {
        Map<String, String> result = service.getAllVariants("user1");

        assertTrue(result.containsKey("exp1"));
        assertTrue(result.containsKey("exp2"));
    }

    // ✅ 3. Validate exp1 distribution (70/30)
    @Test
    void shouldFollow70_30DistributionForExp1() {

        int countA = 0;
        int countB = 0;
        int total = 1000;

        for (int i = 0; i < total; i++) {
            Map<String, String> result =
                    service.getAllVariants("user" + i);

            if ("A".equals(result.get("exp1"))) countA++;
            else if ("B".equals(result.get("exp1"))) countB++;
        }

        // Allow tolerance
        assertTrue(countA > 650 && countA < 750);
        assertTrue(countB > 250 && countB < 350);
    }

    // ✅ 4. Validate exp2 distribution (50/50)
    @Test
    void shouldFollow50_50DistributionForExp2() {

        int countA = 0;
        int countB = 0;
        int total = 1000;

        for (int i = 0; i < total; i++) {
            Map<String, String> result =
                    service.getAllVariants("user" + i);

            if ("A".equals(result.get("exp2"))) countA++;
            else if ("B".equals(result.get("exp2"))) countB++;
        }

        // Allow tolerance
        assertTrue(countA > 450 && countA < 550);
        assertTrue(countB > 450 && countB < 550);
    }

    // ✅ 5. Ensure no null values
    @Test
    void shouldNotReturnNullVariants() {
        Map<String, String> result = service.getAllVariants("user1");

        for (String variant : result.values()) {
            assertNotNull(variant);
        }
    }
}
