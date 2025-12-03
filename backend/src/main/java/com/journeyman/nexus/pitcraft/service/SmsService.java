package com.journeyman.nexus.pitcraft.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty()) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void sendCheckIn(String meatName) {
        // Only attempt to send if configured
        if (accountSid == null || accountSid.isEmpty()) return;

        String msg = String.format(
                "🔥 NEXUS ALERT: The %s is finishing in 15 mins.\n" +
                        "Reply 'ADD 30' to extend or 'DONE' to rest.", meatName
        );

        try {
            Message.creator(new PhoneNumber(userNumber), new PhoneNumber(fromNumber), msg).create();
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

    public void sendReply(String text) {
        if (accountSid == null || accountSid.isEmpty()) return;
        Message.creator(new PhoneNumber(userNumber), new PhoneNumber(fromNumber), text).create();
    }
}