package com.challenge.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for SQL solution submission.
 * Contains the final SQL query to be submitted to the webhook URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private String finalQuery;
}
