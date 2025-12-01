package com.challenge.webhook;

import com.challenge.webhook.service.ChallengeOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebhookSqlChallengeApplication {

    private static final Logger logger = LoggerFactory.getLogger(WebhookSqlChallengeApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WebhookSqlChallengeApplication.class, args);
    }

    /**
     * CommandLineRunner bean that triggers the challenge workflow on application startup.
     * Executes after Spring context initialization is complete.
     * Handles any uncaught exceptions gracefully to prevent application crash.
     *
     * @param orchestrator the ChallengeOrchestrator service to execute the workflow
     * @return CommandLineRunner that executes the challenge
     */
    @Bean
    public CommandLineRunner runChallenge(ChallengeOrchestrator orchestrator) {
        return args -> {
            try {
                logger.info("Application started successfully. Initiating challenge workflow...");
                orchestrator.executeChallenge();
                logger.info("Challenge workflow execution completed.");
            } catch (Exception e) {
                logger.error("Failed to execute challenge workflow: {}", e.getMessage(), e);
                logger.error("Application will continue running despite workflow failure.");
                // Don't rethrow - allow application to continue running
                // This ensures graceful handling of any uncaught exceptions
            }
        };
    }
}
