package com.challenge.webhook.service;

import com.challenge.webhook.model.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for determining which SQL question to solve
 * based on the registration number.
 */
@Service
public class QuestionAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionAssignmentService.class);

    /**
     * Determines which question to solve based on the registration number.
     * Extracts the last two digits and assigns:
     * - QUESTION_1 if the last two digits form an odd number
     * - QUESTION_2 if the last two digits form an even number
     *
     * @param regNo the registration number
     * @return QuestionType.QUESTION_1 for odd, QuestionType.QUESTION_2 for even
     * @throws IllegalArgumentException if regNo is null, empty, or has fewer than 2 characters
     */
    public QuestionType determineQuestion(String regNo) {
        logger.info("Determining question assignment for registration number: {}", regNo);
        logger.debug("Validating registration number format");
        
        validateRegistrationNumber(regNo);
        
        // Extract last two digits
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        logger.debug("Extracted last two digits: {}", lastTwoDigits);
        
        // Parse to integer
        int lastTwoDigitsValue;
        try {
            lastTwoDigitsValue = Integer.parseInt(lastTwoDigits);
        } catch (NumberFormatException e) {
            logger.error("Failed to parse last two digits as number: {}", lastTwoDigits, e);
            throw new IllegalArgumentException(
                "Last two characters of registration number must be numeric digits: " + regNo, e
            );
        }
        
        // Determine question based on odd/even
        QuestionType questionType;
        if (lastTwoDigitsValue % 2 == 0) {
            questionType = QuestionType.QUESTION_2;
            logger.info("Last two digits ({}) are even, assigning QUESTION_2", lastTwoDigitsValue);
        } else {
            questionType = QuestionType.QUESTION_1;
            logger.info("Last two digits ({}) are odd, assigning QUESTION_1", lastTwoDigitsValue);
        }
        
        return questionType;
    }

    /**
     * Validates the registration number format.
     *
     * @param regNo the registration number to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRegistrationNumber(String regNo) {
        if (regNo == null) {
            throw new IllegalArgumentException("Registration number cannot be null");
        }
        
        if (regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty");
        }
        
        if (regNo.length() < 2) {
            throw new IllegalArgumentException(
                "Registration number must have at least 2 characters: " + regNo
            );
        }
    }
}
