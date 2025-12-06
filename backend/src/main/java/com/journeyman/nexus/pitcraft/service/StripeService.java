package com.journeyman.nexus.pitcraft.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Value("${stripe.currency}")
    private String currency;

    @PostConstruct
    public void init() {
        // Initialize the static Stripe library with your key
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(double amountDollars) throws StripeException {
        // Stripe works in "cents" (integers), not decimals.
        // $50.00 -> 5000 cents
        long amountInCents = (long) (amountDollars * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }
}