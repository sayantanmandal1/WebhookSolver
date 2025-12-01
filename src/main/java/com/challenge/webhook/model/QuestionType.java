package com.challenge.webhook.model;

/**
 * Enum representing the SQL question assignment.
 * Question assignment is based on the last two digits of the registration number:
 * - QUESTION_1: Assigned when last two digits form an odd number
 * - QUESTION_2: Assigned when last two digits form an even number
 */
public enum QuestionType {
    QUESTION_1,
    QUESTION_2
}
