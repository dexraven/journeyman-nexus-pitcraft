package com.journeyman.nexus.pitcraft.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    // It's just a POJO, so we can test it directly without Spring context
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_Returns400() {
        EntityNotFoundException ex = new EntityNotFoundException("MeatSession not found");

        ProblemDetail response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("MeatSession not found", response.getDetail());
    }

    @Test
    void handleNotFound_Returns404() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "Invalid endpoint");

        ProblemDetail response = handler.handleWrongUrl(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Endpoint not found: Invalid endpoint", response.getDetail());
    }

    @Test
    void handleConflict_Returns409() {
        IllegalStateException ex = new IllegalStateException("Too late to cancel");

        ProblemDetail response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Too late to cancel", response.getDetail());
        assertEquals("Conflict", response.getTitle());
    }

    @Test
    void handleBadRequest_Returns400() {
        // 1. Arrange
        IllegalArgumentException ex = new IllegalArgumentException("ID cannot be null");

        // 2. Act
        ProblemDetail response = handler.handleBadRequest(ex);

        // 3. Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("ID cannot be null", response.getDetail());
    }
}