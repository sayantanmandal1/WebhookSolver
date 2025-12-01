package com.challenge.webhook.service;

import com.challenge.webhook.config.ChallengeProperties;
import com.challenge.webhook.model.UserDetails;
import com.challenge.webhook.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service responsible for generating webhooks by calling the webhook generator API.
 * Handles webhook generation requests and response parsing.
 */
@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate;
    private final ChallengeProperties properties;

    public WebhookService(RestTemplate restTemplate, ChallengeProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * Generates a webhook by sending user details to the webhook generator API.
     * 
     * @param userDetails the user details to send in the request
     * @return WebhookResponse containing the webhook URL and access token
     * @throws WebhookGenerationException if webhook generation fails
     */
    public WebhookResponse generateWebhook(UserDetails userDetails) {
        String apiUrl = properties.getApi().getWebhookGeneratorUrl();
        
        logger.info("Generating webhook for user: {}", userDetails.getEmail());
        logger.debug("Webhook generator API URL: {}", apiUrl);

        try {
            // Build request with JSON body
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserDetails> request = new HttpEntity<>(userDetails, headers);

            // Send POST request
            logger.info("Sending POST request to webhook generator API");
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                    apiUrl, 
                    request, 
                    WebhookResponse.class
            );

            // Parse and validate response
            WebhookResponse webhookResponse = response.getBody();
            
            if (webhookResponse == null) {
                logger.error("Received null response body from webhook generator API");
                throw new WebhookGenerationException("Webhook generator API returned null response body");
            }

            if (webhookResponse.getWebhook() == null || webhookResponse.getWebhook().isEmpty()) {
                logger.error("Webhook URL is missing or empty in response");
                throw new WebhookGenerationException("Webhook URL is missing in API response");
            }

            if (webhookResponse.getAccessToken() == null || webhookResponse.getAccessToken().isEmpty()) {
                logger.error("Access token is missing or empty in response");
                throw new WebhookGenerationException("Access token is missing in API response");
            }

            logger.info("Successfully generated webhook: {}", webhookResponse.getWebhook());
            logger.debug("Access token received (length: {})", webhookResponse.getAccessToken().length());

            return webhookResponse;

        } catch (RestClientException e) {
            logger.error("Network error while calling webhook generator API: {}", e.getMessage(), e);
            throw new WebhookGenerationException("Failed to generate webhook due to network error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during webhook generation: {}", e.getMessage(), e);
            throw new WebhookGenerationException("Unexpected error during webhook generation: " + e.getMessage(), e);
        }
    }

    /**
     * Exception thrown when webhook generation fails.
     */
    public static class WebhookGenerationException extends RuntimeException {
        public WebhookGenerationException(String message) {
            super(message);
        }

        public WebhookGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
