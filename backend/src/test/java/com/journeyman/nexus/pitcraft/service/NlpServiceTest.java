package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NlpServiceTest {

    @Mock
    private ChatModel chatModel; // <--- Mock the Model

    @InjectMocks
    private NlpService nlpService;

    @Test
    void parseUserIntent_Success() {
        // 1. Prepare JSON
        String mockJson = """
                {
                    "action": "EXTEND_TIME",
                    "minutesModifier": 30,
                    "meatType": "BRISKET"
                }
                """;

        // 2. Mock Response
        Generation generation = new Generation(mockJson);
        ChatResponse mockResponse = new ChatResponse(List.of(generation));

        // This line is no longer deprecated because ChatModel owns .call()
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // 3. Test
        PitCommand result = nlpService.parseUserIntent("Add 30 mins", List.of("BRISKET"));

        assertEquals(PitCommand.Action.EXTEND_TIME, result.action());
        assertEquals(30, result.minutesModifier());
    }

    @Test
    void parseUserIntent_AiFailure() {
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("API Down"));

        PitCommand result = nlpService.parseUserIntent("Add 30 mins", Collections.emptyList());

        assertEquals(PitCommand.Action.UNKNOWN, result.action());
    }

    @Test
    void parseUserIntent_BadJson() {
        Generation generation = new Generation("I am just a robot");
        ChatResponse mockResponse = new ChatResponse(List.of(generation));

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        PitCommand result = nlpService.parseUserIntent("Add 30 mins", Collections.emptyList());

        assertEquals(PitCommand.Action.UNKNOWN, result.action());
    }
}