package com.journeyman.nexus.pitcraft.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    // Pure POJO test - no Spring Context needed
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_Returns404() {
        // Arrange
        EntityNotFoundException ex = new EntityNotFoundException("Session not found");

        // Act
        ProblemDetail response = handler.handleNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Session not found", response.getDetail());
    }

    @Test
    void handleConflict_Returns409_WithCustomTitle() {
        // Arrange
        IllegalStateException ex = new IllegalStateException("Too late to cancel");

        // Act
        ProblemDetail response = handler.handleConflict(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Too late to cancel", response.getDetail());
        // Verify the specific title we added for the frontend logic
        assertEquals("CANCELLATION_DENIED", response.getTitle());
    }

    @Test
    void handleBadRequest_Returns400() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input data");

        // Act
        ProblemDetail response = handler.handleBadRequest(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid input data", response.getDetail());
    }

    @Test
    void handleGeneral_Returns500() {
        // Arrange
        RuntimeException ex = new RuntimeException("Unexpected Null Pointer");

        // Act
        ProblemDetail response = handler.handleGeneral(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        // Ensure the message format matches "Internal Server Error: [msg]"
        assertEquals("Internal Server Error: Unexpected Null Pointer", response.getDetail());
    }
}