package com.challenge.webhook.service;

import com.challenge.webhook.model.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for generating SQL query solutions
 * based on the assigned question type.
 */
@Service
public class SqlSolverService {

    private static final Logger logger = LoggerFactory.getLogger(SqlSolverService.class);

    /**
     * SQL solution for Question 1 (odd registration numbers).
     * Finds the highest salaried employee per department,
     * excluding payments made on the 1st day of the month.
     */
    private static final String QUESTION_1_SQL = 
        "SELECT \n" +
        "    d.DEPARTMENT_NAME,\n" +
        "    SUM(p.AMOUNT) AS SALARY,\n" +
        "    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME,\n" +
        "    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE\n" +
        "FROM DEPARTMENT d\n" +
        "INNER JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT\n" +
        "INNER JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID\n" +
        "WHERE DAY(p.PAYMENT_TIME) != 1\n" +
        "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME, e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB\n" +
        "HAVING SUM(p.AMOUNT) = (\n" +
        "    SELECT MAX(total_salary)\n" +
        "    FROM (\n" +
        "        SELECT SUM(p2.AMOUNT) AS total_salary\n" +
        "        FROM EMPLOYEE e2\n" +
        "        INNER JOIN PAYMENTS p2 ON e2.EMP_ID = p2.EMP_ID\n" +
        "        WHERE e2.DEPARTMENT = d.DEPARTMENT_ID\n" +
        "        AND DAY(p2.PAYMENT_TIME) != 1\n" +
        "        GROUP BY e2.EMP_ID\n" +
        "    ) AS dept_salaries\n" +
        ")\n" +
        "ORDER BY d.DEPARTMENT_ID";

    /**
     * SQL solution for Question 2 (even registration numbers).
     * Filters employees with payments > 70,000 and calculates
     * average age per department with employee list.
     */
    private static final String QUESTION_2_SQL = 
        "SELECT \n" +
        "    d.DEPARTMENT_NAME,\n" +
        "    ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE,\n" +
        "    GROUP_CONCAT(\n" +
        "        CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) \n" +
        "        ORDER BY e.EMP_ID \n" +
        "        SEPARATOR ', '\n" +
        "    ) AS EMPLOYEE_LIST\n" +
        "FROM DEPARTMENT d\n" +
        "INNER JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT\n" +
        "INNER JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID\n" +
        "WHERE p.AMOUNT > 70000\n" +
        "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME\n" +
        "ORDER BY d.DEPARTMENT_ID DESC";

    /**
     * Solves the SQL problem based on the assigned question type.
     *
     * @param questionType the type of question to solve (QUESTION_1 or QUESTION_2)
     * @return the SQL query string for the given question type
     * @throws IllegalArgumentException if questionType is null
     */
    public String solveProblem(QuestionType questionType) {
        logger.info("Solving SQL problem for question type: {}", questionType);
        
        if (questionType == null) {
            logger.error("Question type is null");
            throw new IllegalArgumentException("Question type cannot be null");
        }

        String sqlQuery;
        switch (questionType) {
            case QUESTION_1:
                logger.info("Returning SQL solution for QUESTION_1 (odd registration numbers)");
                sqlQuery = QUESTION_1_SQL;
                break;
            case QUESTION_2:
                logger.info("Returning SQL solution for QUESTION_2 (even registration numbers)");
                sqlQuery = QUESTION_2_SQL;
                break;
            default:
                logger.error("Unknown question type: {}", questionType);
                throw new IllegalArgumentException("Unknown question type: " + questionType);
        }
        
        logger.debug("Generated SQL query (length: {} characters)", sqlQuery.length());
        logger.debug("SQL Query:\n{}", sqlQuery);
        
        return sqlQuery;
    }
}
