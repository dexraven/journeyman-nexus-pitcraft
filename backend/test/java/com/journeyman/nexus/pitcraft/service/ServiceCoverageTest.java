package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCoverageTest {

    @InjectMocks
    private SmsService smsService;

    // We mock the ChatClient chain for NlpService
    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec responseSpec;

    @Test
    void testSmsService_Safeguards() {
        // Case 1: Keys are missing (Default state in test)
        // init() should not crash, sendCheckIn should return early
        smsService.init();
        smsService.sendCheckIn("Brisket");
        smsService.sendReply("Hello");
        // Pass if no exception thrown

        // Case 2: We inject a fake key to test the "Exception" block
        ReflectionTestUtils.setField(smsService, "accountSid", "FAKE_SID");
        ReflectionTestUtils.setField(smsService, "fromNumber", "+1555");
        ReflectionTestUtils.setField(smsService, "userNumber", "+1555");

        // This will try to call Twilio and fail (because key is fake)
        // We want to ensure it catches the exception and prints to stderr, not crashes app
        assertDoesNotThrow(() -> smsService.sendCheckIn("Brisket"));
    }

    @Test
    void testNlpService_Fallback() {
        // We need to manually construct NlpService because of the Builder pattern in constructor
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        when(builder.build()).thenReturn(chatClient);
        NlpService nlpService = new NlpService(builder);

        // Case 1: AI Throws Exception (e.g., API Down)
        when(chatClient.prompt()).thenThrow(new RuntimeException("API Down"));

        PitCommand result = nlpService.parseUserIntent("Add 30 mins", java.util.Collections.emptyList());

        assertEquals(PitCommand.Action.UNKNOWN, result.action());
    }
}