package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NlpService {

    private final ChatModel chatModel;

    public NlpService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public PitCommand parseUserIntent(String userText, List<String> activeMeats) {
        BeanOutputParser<PitCommand> parser = new BeanOutputParser<>(PitCommand.class);

        PromptTemplate template = getPromptTemplate();
        template.add("activeMeats", String.join(", ", activeMeats));
        template.add("format", parser.getFormat());

        try {
            // .call() is the native method of ChatModel, so no warnings here!
            String response = chatModel.call(template.create()).getResult().getOutput().getContent();
            return parser.parse(response);

        } catch (Exception e) {
            System.err.println("AI Parse Error: " + e.getMessage());
            return new PitCommand(PitCommand.Action.UNKNOWN, null, 0);
        }
    }

    @NonNull
    private static PromptTemplate getPromptTemplate() {
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

        return new PromptTemplate(promptText);
    }
}