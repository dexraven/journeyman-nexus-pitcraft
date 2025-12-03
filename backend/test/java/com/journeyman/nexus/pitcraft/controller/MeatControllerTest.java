package com.journeyman.nexus.pitcraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.service.MeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeatController.class)
class MeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeatService meatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void planMeat_ReturnsSession() throws Exception {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.BEEF_BRISKET);
        req.setWeightInLbs(12.0);

        MeatSession session = new MeatSession();
        session.setId("uuid-1");
        session.setMeatType(MeatType.BEEF_BRISKET);
        session.setStatus(CookStatus.PLANNED);

        when(meatService.createSession(any())).thenReturn(session);

        mockMvc.perform(post("/api/v1/meat/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.meatType").value("BEEF_BRISKET"));
    }

    @Test
    void cancelMeat_Conflict_WhenTooLate() throws Exception {
        // Simulate the service throwing the "Point of No Return" error
        doThrow(new IllegalStateException("Inventory consumed"))
                .when(meatService).cancelSession("active-id");

        mockMvc.perform(put("/api/v1/meat/active-id/cancel"))
                .andExpect(status().isConflict()) // Expect 409
                .andExpect(jsonPath("$.error").value("CANCELLATION_DENIED"));
    }
}