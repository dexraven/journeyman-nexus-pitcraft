package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Map<String, Double> payload) {
        if (payload == null || !payload.containsKey("amount")) {
            throw new IllegalArgumentException("Payload must contain 'amount'");
        }

        double amount = payload.get("amount");

        try {
            PaymentIntent intent = stripeService.createPaymentIntent(amount);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", intent.getClientSecret(),
                    "id", intent.getId()
            ));
        } catch (StripeException e) {
            // The controller catches the checked exception and returns 400
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}