package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.service.SmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SmsControllerTest {

    @Mock
    private SmsService smsService;

    @InjectMocks
    private SmsController smsController;

    @Test
    void handleReply_Success() {
        String fromNumber = "+15550001234";
        String bodyText = "Add 30 mins";

        String result = smsController.handleReply(fromNumber, bodyText);

        assertEquals("<Response><Message/></Response>", result);

        verify(smsService).handleIncomingSms(fromNumber, bodyText);
    }

    @Test
    void handleReply_BadRequest_ThrowsException() {
        // 1. Act & Assert
        // We expect the controller to throw IllegalArgumentException when inputs are null
        assertThrows(IllegalArgumentException.class, () -> {
            smsController.handleReply(null, null);
        });

        // 2. Verify
        // Ensure we never bothered the service with bad data
        verifyNoInteractions(smsService);
    }
}