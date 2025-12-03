package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NlpService {

    private final ChatClient chatClient;

    public NlpService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public PitCommand parseUserIntent(String userText, List<String> activeMeats) {
        String prompt = """
            You are a BBQ Assistant. Translate text to commands.
            Active meats: %s.
            
            RULES:
            1. "stall", "wait", "add time" -> EXTEND_TIME
            2. "done", "pulling", "resting" -> MARK_DONE
            3. "start", "firing" -> START_COOK
            4. Extract minutes (default 30).
            
            Return JSON matching PitCommand structure.
            """.formatted(String.join(", ", activeMeats));

        try {
            return chatClient.prompt()
                    .system(prompt)
                    .user(userText)
                    .call()
                    .entity(PitCommand.class);
        } catch (Exception e) {
            // Fallback if AI fails or no key
            return new PitCommand(PitCommand.Action.UNKNOWN, null, 0);
        }
    }
}