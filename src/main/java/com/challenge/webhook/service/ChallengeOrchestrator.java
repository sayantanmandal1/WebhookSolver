package com.challenge.webhook.service;

import com.challenge.webhook.config.ChallengeProperties;
import com.challenge.webhook.model.QuestionType;
import com.challenge.webhook.model.UserDetails;
import com.challenge.webhook.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrator service that coordinates the complete challenge workflow.
 * Executes all steps in sequence: webhook generation, question assignment,
 * SQL solving, and solution submission.
 */
@Service
public class ChallengeOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeOrchestrator.class);

    private final WebhookService webhookService;
    private final QuestionAssignmentService questionAssignmentService;
    private final SqlSolverService sqlSolverService;
    private final SubmissionService submissionService;
    private final ChallengeProperties properties;

    public ChallengeOrchestrator(
            WebhookService webhookService,
            QuestionAssignmentService questionAssignmentService,
            SqlSolverService sqlSolverService,
            SubmissionService submissionService,
            ChallengeProperties properties) {
        this.webhookService = webhookService;
        this.questionAssignmentService = questionAssignmentService;
        this.sqlSolverService = sqlSolverService;
        this.submissionService = submissionService;
        this.properties = properties;
    }

    /**
     * Executes the complete challenge workflow.
     * Steps:
     * 1. Generate webhook and get access token
     * 2. Determine which question to solve based on registration number
     * 3. Get the SQL query solution for the assigned question
     * 4. Submit the solution to the webhook URL
     * 
     * Logs comprehensive information at each step and provides a workflow summary.
     */
    public void executeChallenge() {
        logger.info("=== Starting Challenge Workflow ===");
        
        try {
            // Step 1: Generate webhook and get access token
            logger.info("Step 1: Generating webhook...");
            UserDetails userDetails = createUserDetails();
            WebhookResponse webhookResponse = webhookService.generateWebhook(userDetails);
            logger.info("Step 1 completed: Webhook URL: {}", webhookResponse.getWebhook());

            // Step 2: Determine which question to solve
            logger.info("Step 2: Determining question assignment...");
            QuestionType questionType = questionAssignmentService.determineQuestion(userDetails.getRegNo());
            logger.info("Step 2 completed: Assigned question: {}", questionType);

            // Step 3: Get SQL query solution
            logger.info("Step 3: Generating SQL solution...");
            String sqlQuery = sqlSolverService.solveProblem(questionType);
            logger.info("Step 3 completed: SQL query generated (length: {} characters)", sqlQuery.length());
            logger.debug("SQL Query: {}", sqlQuery);

            // Step 4: Submit solution
            logger.info("Step 4: Submitting solution...");
            submissionService.submitSolution(
                    webhookResponse.getWebhook(),
                    webhookResponse.getAccessToken(),
                    sqlQuery
            );
            logger.info("Step 4 completed: Solution submitted successfully");

            // Workflow summary
            logger.info("=== Challenge Workflow Completed Successfully ===");
            logger.info("Summary:");
            logger.info("  - User: {} ({})", userDetails.getName(), userDetails.getEmail());
            logger.info("  - Registration Number: {}", userDetails.getRegNo());
            logger.info("  - Assigned Question: {}", questionType);
            logger.info("  - Webhook URL: {}", webhookResponse.getWebhook());
            logger.info("  - Submission Status: SUCCESS");

        } catch (WebhookService.WebhookGenerationException e) {
            logger.error("=== Challenge Workflow Failed at Step 1: Webhook Generation ===");
            logger.error("Error: {}", e.getMessage(), e);
            throw new ChallengeExecutionException("Workflow failed during webhook generation", e);
            
        } catch (IllegalArgumentException e) {
            logger.error("=== Challenge Workflow Failed: Invalid Input ===");
            logger.error("Error: {}", e.getMessage(), e);
            throw new ChallengeExecutionException("Workflow failed due to invalid input", e);
            
        } catch (Exception e) {
            logger.error("=== Challenge Workflow Failed: Unexpected Error ===");
            logger.error("Error: {}", e.getMessage(), e);
            throw new ChallengeExecutionException("Workflow failed due to unexpected error", e);
        }
    }

    /**
     * Creates UserDetails object from configuration properties.
     *
     * @return UserDetails populated with values from configuration
     */
    private UserDetails createUserDetails() {
        ChallengeProperties.User user = properties.getUser();
        return new UserDetails(user.getName(), user.getRegNo(), user.getEmail());
    }

    /**
     * Exception thrown when the challenge workflow execution fails.
     */
    public static class ChallengeExecutionException extends RuntimeException {
        public ChallengeExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
