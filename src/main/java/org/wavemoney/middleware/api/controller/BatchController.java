package org.wavemoney.middleware.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job customerJob;

    @PostMapping("/start")
    public ResponseEntity<String> startBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(customerJob, jobParameters);
            return ResponseEntity.ok("Batch job has been started");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to start batch job: " + e.getMessage());
        }
    }
}
