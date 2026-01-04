package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    // Twilio hits this URL when you get a text
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleReply(@RequestParam("From") String from, @RequestParam("Body") String body) {

        if (from == null || body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing 'From' or 'Body'");
        }
        // Delegate logic to the service
        smsService.handleIncomingSms(from, body);

        // Return TwiML (XML) to tell Twilio we got it
        return "<Response><Message/></Response>";
    }
}