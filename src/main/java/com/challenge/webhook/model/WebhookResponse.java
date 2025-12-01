package com.challenge.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from the webhook generator API.
 * Contains the webhook URL and JWT access token for submission.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {
    private String webhook;
    private String accessToken;
}
