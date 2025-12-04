package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NlpService {

    private final ChatClient chatClient;

    // FIX 1: Inject ChatClient directly (No Builder needed in 0.8.1)
    public NlpService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public PitCommand parseUserIntent(String userText, List<String> activeMeats) {
        // FIX 2: Use BeanOutputParser for structured JSON conversion
        BeanOutputParser<PitCommand> parser = new BeanOutputParser<>(PitCommand.class);

        String promptText = """
            You are a BBQ Assistant. Translate text to commands.
            Active meats: {activeMeats}.
            
            RULES:
            1. "stall", "wait", "add time" -> EXTEND_TIME
            2. "done", "pulling", "resting" -> MARK_DONE
            3. "start", "firing" -> START_COOK
            4. Extract minutes (default 30).
            
            Return JSON matching PitCommand structure.
            {format}
            """;

        // FIX 3: Use PromptTemplate to inject variables and the JSON format
        PromptTemplate template = new PromptTemplate(promptText);
        template.add("activeMeats", String.join(", ", activeMeats));
        template.add("format", parser.getFormat());

        try {
            // FIX 4: Standard .call() instead of fluent .prompt()
            String response = chatClient.call(template.create()).getResult().getOutput().getContent();
            return parser.parse(response);

        } catch (Exception e) {
            // Fallback
            System.err.println("AI Parse Error: " + e.getMessage());
            return new PitCommand(PitCommand.Action.UNKNOWN, null, 0);
        }
    }
}