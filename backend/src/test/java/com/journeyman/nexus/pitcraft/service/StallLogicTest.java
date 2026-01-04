package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.domain.TemperatureLog;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.repository.TemperatureLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StallLogicTest {

    @Mock private MeatSessionRepository sessionRepo;
    @Mock private TemperatureLogRepository tempRepo;
    @Mock private SmsService smsService;

    @InjectMocks
    private MeatService meatService;

    @Test
    void logTemperature_DetectsStall() {
        // 1. Arrange: A session currently cooking
        MeatSession session = new MeatSession();
        session.setId("session-1");
        session.setMeatType(MeatType.BEEF_BRISKET);
        session.setStatus(CookStatus.COOKING);
        session.setAlertSent(false); // Alert hasn't fired yet

        when(sessionRepo.findById("session-1")).thenReturn(Optional.of(session));

        // 2. Arrange: History showing a stall
        // 45 mins ago it was 160.0 degrees
        TemperatureLog oldLog = new TemperatureLog(session, 160.0);
        oldLog.setLogTime(LocalDateTime.now().minusMinutes(45));

        when(tempRepo.findRecentLogs(eq("session-1"), any(LocalDateTime.class)))
                .thenReturn(List.of(oldLog));

        // 3. Act: Log new temp (161.0 degrees)
        // It only rose 1 degree in 45 mins -> STALL!
        meatService.logTemperature("session-1", 161.0);

        // 4. Assert
        verify(smsService).sendStallAlert(contains("BRISKET"), eq(161.0));

        // Ensure we updated the session to prevent duplicate alerts
        verify(sessionRepo).save(session);
    }

    @Test
    void logTemperature_NoStall_IfRisingFast() {
        MeatSession session = new MeatSession();
        session.setId("session-1");
        session.setStatus(CookStatus.COOKING);

        when(sessionRepo.findById("session-1")).thenReturn(Optional.of(session));

        // 45 mins ago it was 150.0 degrees
        TemperatureLog oldLog = new TemperatureLog(session, 150.0);

        when(tempRepo.findRecentLogs(any(), any())).thenReturn(List.of(oldLog));

        // Act: Current temp is 170.0 (Rose 20 degrees -> No Stall)
        meatService.logTemperature("session-1", 170.0);

        // Assert
        verify(smsService, never()).sendStallAlert(any(), anyDouble());
    }
}