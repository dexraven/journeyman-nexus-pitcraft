package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.service.NlpService;
import com.journeyman.nexus.pitcraft.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsWebhookController {

    private final MeatSessionRepository repository;
    private final NlpService nlpService;
    private final SmsService smsService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void handleReply(@RequestParam("Body") String body) {

        List<MeatSession> active = repository.findByStatus(CookStatus.COOKING);
        List<String> names = active.stream().map(m -> m.getMeatType().toString()).toList();

        PitCommand cmd = nlpService.parseUserIntent(body, names);

        if (cmd.action() == PitCommand.Action.EXTEND_TIME && !active.isEmpty()) {
            MeatSession target = active.get(0); // Simplified: pick first active
            target.setServingTime(target.getServingTime().plusMinutes(cmd.minutesModifier()));
            target.setAlertSent(false);
            repository.save(target);
            smsService.sendReply("Added " + cmd.minutesModifier() + " mins.");
        }
        else if (cmd.action() == PitCommand.Action.MARK_DONE && !active.isEmpty()) {
            MeatSession target = active.get(0);
            target.setStatus(CookStatus.RESTING);
            repository.save(target);
            smsService.sendReply("Meat is now resting.");
        }
        else {
            smsService.sendReply("Command not recognized.");
        }
    }
}