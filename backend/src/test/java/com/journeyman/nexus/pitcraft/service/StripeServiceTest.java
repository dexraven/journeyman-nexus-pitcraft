package com.journeyman.nexus.pitcraft.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @InjectMocks
    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        // Manually inject values since @Value doesn't work in pure unit tests
        ReflectionTestUtils.setField(stripeService, "secretKey", "sk_test_123");
        ReflectionTestUtils.setField(stripeService, "currency", "usd");

        // Run init to set the API key
        stripeService.init();
    }

    @Test
    void createPaymentIntent_Success() throws StripeException {
        // 1. Mock the Static Class (PaymentIntent)
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {

            // 2. Prepare the fake return object
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            // When .create() is called with ANY params, return our mock object
            mockedPaymentIntent.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockIntent);

            // 3. Act
            // We pass $50.00
            PaymentIntent result = stripeService.createPaymentIntent(50.00);

            // 4. Assert
            assertNotNull(result);
            assertEquals(mockIntent, result);

            // Verify arguments (Optional/Advanced: Check if cents conversion worked)
            mockedPaymentIntent.verify(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)));
        }
    }
}