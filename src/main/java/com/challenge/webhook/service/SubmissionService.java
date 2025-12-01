package com.challenge.webhook.service;

import com.challenge.webhook.model.SubmissionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service responsible for submitting SQL solutions to the webhook URL.
 * Handles JWT authentication and proper request formatting.
 */
@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final RestTemplate restTemplate;

    public SubmissionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Submits the SQL solution to the webhook URL with JWT authentication.
     *
     * @param webhookUrl the URL to submit the solution to
     * @param accessToken the JWT token for authentication
     * @param sqlQuery the SQL query solution to submit
     * @throws RestClientException if the submission fails
     */
    public void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        logger.info("Preparing to submit SQL solution to webhook URL: {}", webhookUrl);

        try {
            // Build the request body
            SubmissionRequest request = new SubmissionRequest(sqlQuery);

            // Build headers with JWT authentication
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            // Create HTTP entity with headers and body
            HttpEntity<SubmissionRequest> entity = new HttpEntity<>(request, headers);

            // Send POST request
            logger.debug("Sending POST request with SQL query: {}", sqlQuery);
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Log success
            logger.info("Solution submitted successfully. Status: {}, Response: {}", 
                    response.getStatusCode(), response.getBody());

        } catch (RestClientException e) {
            // Log error details
            logger.error("Failed to submit solution to webhook URL: {}. Error: {}", 
                    webhookUrl, e.getMessage(), e);
            throw e;
        }
    }
}
