package com.dev.org.advice;

import com.dev.org.exception.ApplicationException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

/**
 * Global exception handler for the application. Handles unchecked exceptions and
 * Spring validation exceptions. Converts exceptions to RFC 7807 Problem Details with
 * consistent structure. Includes centralized logging and trace ID propagation.
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        log.error("Invalid request", ex);
        return problemDetail;
    }

    /**
     * Handles custom unchecked ApplicationException.
     */
    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException ex, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail(ex.getMessage());

        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        log.error("Application exception", ex);
        return problemDetail;
    }

    /**
     * Handles Spring's @Valid validation failures (400 Bad Request).
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationException(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> {
                            String fieldName = error.getField();
                            String errorMessage = error.getDefaultMessage();
                            fieldErrors.put(fieldName, errorMessage);
                        });

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("Validation failed");
        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        log.error("Validation failed");

        return problemDetail;
    }

    /**
     * Handles type mismatch in request parameters (400 Bad Request).
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ProblemDetail handleTypeMismatch(ServerWebInputException ex, ServerWebExchange exchange) {
        String reason = ex.getReason() != null ? ex.getReason() : "Invalid request input";

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(reason);
        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        log.error("Type mismatch");

        return problemDetail;
    }

    /**
     * Handles all other uncaught exceptions (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("An unexpected error occurred. Please try again later.");
        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        log.error("Unhandled exception", ex);

        return problemDetail;
    }
}
