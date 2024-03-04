package com.splawrence.ecommercepro.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Represents an error message.
 */
@Data
public class ErrorMessage {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private String description;

    /**
     * Constructs a new ErrorMessage object with the specified parameters.
     *
     * @param statusCode  the status code of the error
     * @param timestamp   the timestamp when the error occurred
     * @param message     the error message
     * @param description the description of the error
     */
    public ErrorMessage(int statusCode, LocalDateTime timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }
}
