package com.journeyman.nexus.pitcraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journeyman.nexus.pitcraft.ai.PitCommand;
import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.service.MeatService;
import com.journeyman.nexus.pitcraft.service.NlpService;
import com.journeyman.nexus.pitcraft.service.SmsService;
import com.journeyman.nexus.pitcraft.strategy.MeatPlanFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// We test Controllers and Services together to ensure flow coverage
@WebMvcTest({MeatController.class, SmsWebhookController.class})
@Import(MeatService.class) // Import the real Service to test it too
class FlowCoverageTest {

    @Autowired MockMvc mockMvc;

    @MockBean MeatSessionRepository repository;
    @MockBean NlpService nlpService;

    @Test
    void testCancelFlow_FullCoverage() throws Exception {
        // 1. Setup Session (PLANNED)
        MeatSession s1 = MeatSession.builder().id("1").status(CookStatus.PLANNED).build();
        when(repository.findById("1")).thenReturn(Optional.of(s1));

        // 2. Success Cancel
        mockMvc.perform(put("/api/v1/meat/1/cancel"))
                .andExpect(status().isNoContent());

        // 3. Point of No Return (BRINING)
        MeatSession s2 = MeatSession.builder().id("2").status(CookStatus.BRINING).build();
        when(repository.findById("2")).thenReturn(Optional.of(s2));

        mockMvc.perform(put("/api/v1/meat/2/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CANCELLATION_DENIED"));

        // 4. Not Found
        when(repository.findById("99")).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/v1/meat/99/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testWebhook_AllCommands() throws Exception {
        MeatSession activeSession = MeatSession.builder()
                .id("1")
                .meatType(MeatType.BEEF_BRISKET)
                .servingTime(LocalDateTime.now())
                .status(CookStatus.COOKING)
                .build();

        when(repository.findByStatus(CookStatus.COOKING)).thenReturn(List.of(activeSession));

        // Case 1: EXTEND_TIME
        when(nlpService.parseUserIntent(any(), any())).thenReturn(
                new PitCommand(PitCommand.Action.EXTEND_TIME, "BEEF_BRISKET", 60)
        );

        mockMvc.perform(post("/api/v1/sms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("Body=Add 60 mins"))
                .andExpect(status().isOk());

        verify(repository, atLeastOnce()).save(any()); // Should save new time

        // Case 2: MARK_DONE
        when(nlpService.parseUserIntent(any(), any())).thenReturn(
                new PitCommand(PitCommand.Action.MARK_DONE, "BEEF_BRISKET", 0)
        );

        mockMvc.perform(post("/api/v1/sms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("Body=Done"))
                .andExpect(status().isOk());

        // Case 3: UNKNOWN
        when(nlpService.parseUserIntent(any(), any())).thenReturn(
                new PitCommand(PitCommand.Action.UNKNOWN, null, 0)
        );
        mockMvc.perform(post("/api/v1/sms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("Body=Garbage"))
                .andExpect(status().isOk());
    }
}