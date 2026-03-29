package com.ab.experiment.system.controller;

import com.ab.experiment.system.service.ExperimentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/experiments")
public class ExperimentController {

    private final ExperimentService service;

    public ExperimentController(ExperimentService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getExperiments(
            @RequestParam String userId
    ) {

        Map<String, String> assignments =
                service.getAllVariants(userId);

        return Map.of(
                "userId", userId,
                "experiments", assignments
        );
    }
}