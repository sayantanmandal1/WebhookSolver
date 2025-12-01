package com.challenge.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User details for webhook generation request.
 * Contains participant information required by the webhook generator API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    private String name;
    private String regNo;
    private String email;
}
