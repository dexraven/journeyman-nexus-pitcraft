package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.service.MeatService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeatControllerTest {

    @Mock
    private MeatService meatService;

    @InjectMocks
    private MeatController meatController;

    // Order Creation test
    @Test
    void addMeat_Success() {
        // Arrange
        MeatRequest request = new MeatRequest();
        request.setType(MeatType.BEEF_BRISKET);
        MeatSession session = new MeatSession();

        when(meatService.createSession(request)).thenReturn(session);

        // Act
        ResponseEntity<MeatSession> response = meatController.addMeat(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(session, response.getBody());
    }

    @Test
    void addMeat_NullInput_ThrowsException() {
        // This validates the input guard we added
        assertThrows(IllegalArgumentException.class, () -> {
            meatController.addMeat(null);
        });

        // Ensure we never called the service
        verifyNoInteractions(meatService);
    }

    // Active orders test

    @Test
    void getActive_ReturnsList() {
        when(meatService.getActiveSessions()).thenReturn(Collections.emptyList());

        ResponseEntity<List<MeatSession>> response = meatController.getActive();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }


    // Order Cancellation tests
    @Test
    void cancelMeat_Success() {
        ResponseEntity<?> response = meatController.cancelMeat("uuid-123");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(meatService).cancelSession("uuid-123");
    }

    @Test
    void cancelMeat_BadInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            meatController.cancelMeat("");
        });
    }

    @Test
    void cancelMeat_Conflict_ThrowsException() {
        doThrow(new IllegalStateException("Too late to cancel"))
                .when(meatService).cancelSession("uuid-123");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            meatController.cancelMeat("uuid-123");
        });

        assertEquals("Too late to cancel", ex.getMessage());
    }

    @Test
    void cancelMeat_NotFound_ThrowsException() {
        doThrow(new EntityNotFoundException("Session with ID unknown-id not found"))
                .when(meatService).cancelSession("unknown-id");

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            meatController.cancelMeat("unknown-id");
        });

        assertEquals("Session with ID unknown-id not found", ex.getMessage());
    }

    // --- TEMP LOGGING TESTS ---

    @Test
    void logTemp_Success() {
        // 1. Arrange
        String id = "session-123";
        Map<String, Double> payload = java.util.Map.of("degrees", 225.5);

        // 2. Act
        ResponseEntity<Void> response = meatController.logTemp(id, payload);

        // 3. Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Verify service was called with correct ID and Value
        verify(meatService).logTemperature("session-123", 225.5);
    }

    @Test
    void logTemp_MissingKey_ThrowsException() {
        // 1. Arrange: Payload exists but has wrong key
        Map<String, Double> payload = java.util.Map.of("wrong_key", 100.0);

        // 2. Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            meatController.logTemp("session-123", payload);
        });

        assertEquals("Payload must contain 'degrees'", ex.getMessage());
        verifyNoInteractions(meatService);
    }
}