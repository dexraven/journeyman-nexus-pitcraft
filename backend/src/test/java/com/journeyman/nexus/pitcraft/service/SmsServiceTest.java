package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    @Mock private ChatModel chatModel;

    @Test
    void testSmsService_Safeguards() {
        smsService.init();
        smsService.sendCheckIn("Brisket");
        smsService.sendReply("Hello");

        ReflectionTestUtils.setField(smsService, "accountSid", "FAKE_SID");
        ReflectionTestUtils.setField(smsService, "fromNumber", "+1555");
        ReflectionTestUtils.setField(smsService, "userNumber", "+1555");

        assertDoesNotThrow(() -> smsService.sendCheckIn("Brisket"));
    }

    @Test
    void testNlpService_Fallback() {
        NlpService nlpService = new NlpService(chatModel);

        // Setup the failure scenario
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("API Down"));

        // Run Logic
        PitCommand result = nlpService.parseUserIntent("Add 30 mins", java.util.Collections.emptyList());

        assertEquals(PitCommand.Action.UNKNOWN, result.action());
    }
}