package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.service.StripeService;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void createPayment_Success() throws StripeException {
        // 1. Arrange
        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getClientSecret()).thenReturn("pi_secret_123");
        when(mockIntent.getId()).thenReturn("pi_123");

        when(stripeService.createPaymentIntent(25.00)).thenReturn(mockIntent);

        Map<String, Double> payload = Map.of("amount", 25.00);

        // 2. Act
        ResponseEntity<Map<String, String>> response = paymentController.createPayment(payload);

        // 3. Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("pi_secret_123", response.getBody().get("clientSecret"));
        assertEquals("pi_123", response.getBody().get("id"));
    }

    @Test
    void createPayment_NullPayload_ThrowsException() {
        // Validation check
        assertThrows(IllegalArgumentException.class, () -> {
            paymentController.createPayment(null);
        });
    }

    @Test
    void createPayment_MissingAmount_ThrowsException() {
        // Payload exists but is empty
        Map<String, Double> emptyPayload = Collections.emptyMap();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            paymentController.createPayment(emptyPayload);
        });

        assertEquals("Payload must contain 'amount'", ex.getMessage());
    }

    @Test
    void createPayment_StripeError_Returns400() throws StripeException {
        // 1. Arrange: Simulate a Stripe failure (e.g. API Down)
        // Since the controller CATCHES this exception, we expect a 400 response, NOT a throw.
        when(stripeService.createPaymentIntent(50.00))
                .thenThrow(new ApiConnectionException("Stripe is down"));

        Map<String, Double> payload = Map.of("amount", 50.00);

        // 2. Act
        ResponseEntity<Map<String, String>> response = paymentController.createPayment(payload);

        // 3. Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Stripe is down", response.getBody().get("error"));
    }
}