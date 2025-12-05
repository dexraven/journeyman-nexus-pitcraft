package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String fromNumber;

    @Value("${user.phone_number}")
    private String userNumber;

    private final NlpService nlpService;
    private final MeatSessionRepository repository; // <--- NEW DEPENDENCY

    // Inject Repository and NlpService
    @Autowired
    public SmsService(NlpService nlpService, MeatSessionRepository repository) {
        this.nlpService = nlpService;
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty()) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void handleIncomingSms(String from, String body) {
        System.out.println("Processing SMS: " + body);

        List<MeatSession> active = repository.findByStatus(CookStatus.COOKING);
        List<String> names = active.stream().map(m -> m.getMeatType().toString()).toList();

        PitCommand cmd = nlpService.parseUserIntent(body, names);

        if (cmd.action() == PitCommand.Action.EXTEND_TIME && !active.isEmpty()) {
            MeatSession target = active.getFirst();
            target.setServingTime(target.getServingTime().plusMinutes(cmd.minutesModifier()));
            target.setAlertSent(false);
            repository.save(target);
            sendReply("Roger that. Added " + cmd.minutesModifier() + " mins.");
        }
        else if (cmd.action() == PitCommand.Action.MARK_DONE && !active.isEmpty()) {
            MeatSession target = active.getFirst();
            target.setStatus(CookStatus.RESTING);
            repository.save(target);
            sendReply("Meat is now resting.");
        }
        else {
            sendReply("I didn't understand that command.");
        }
    }

    public void sendCheckIn(String meatName) {
        if (accountSid == null || accountSid.isEmpty()) return;
        String msg = "🔥 NEXUS ALERT: " + meatName + " is finishing soon. Reply 'ADD 30' or 'DONE'.";
        try {
            Message.creator(new PhoneNumber(userNumber), new PhoneNumber(fromNumber), msg).create();
        } catch (Exception e) {
            System.err.println("Twilio Error: " + e.getMessage());
        }
    }

    public void sendReply(String text) {
        if (accountSid == null || accountSid.isEmpty()) return;
        try {
            Message.creator(new PhoneNumber(userNumber), new PhoneNumber(fromNumber), text).create();
        } catch (Exception e) {
            System.err.println("Twilio Error: " + e.getMessage());
        }
    }

    public void sendStallAlert(String meatName, double temp) {
        if (accountSid == null || accountSid.isEmpty()) return;

        String msg = String.format("⚠️ STALL DETECTED! %s is stuck at %.1fF. Time to wrap?", meatName, temp);

        try {
            Message.creator(new PhoneNumber(userNumber), new PhoneNumber(fromNumber), msg).create();
        } catch (Exception e) {
            System.err.println("Twilio Error: " + e.getMessage());
        }
    }
}