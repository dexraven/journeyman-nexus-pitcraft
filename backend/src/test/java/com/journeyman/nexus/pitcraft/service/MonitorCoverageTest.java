package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitorCoverageTest {

    @Mock
    MeatSessionRepository repository;

    @Mock
    SmsService smsService;

    @InjectMocks
    CookingMonitor monitor;

    @Test
    void testCheckMeatStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Meat that is READY to alert (Serving time is in 10 mins)
        MeatSession ready = MeatSession.builder()
                .meatType(MeatType.BEEF_BRISKET)
                .servingTime(now.plusMinutes(10)) // < 15 mins away
                .alertSent(false)
                .status(CookStatus.COOKING)
                .build();

        // 2. Meat that is NOT ready (Serving time is in 60 mins)
        MeatSession notReady = MeatSession.builder()
                .meatType(MeatType.PORK_RIBS)
                .servingTime(now.plusMinutes(60))
                .alertSent(false)
                .status(CookStatus.COOKING)
                .build();

        // 3. Meat that ALREADY alerted
        MeatSession alreadySent = MeatSession.builder()
                .servingTime(now.plusMinutes(5))
                .alertSent(true)
                .status(CookStatus.COOKING)
                .build();

        when(repository.findByStatus(CookStatus.COOKING))
                .thenReturn(List.of(ready, notReady, alreadySent));

        // Act
        monitor.checkMeatStatus();

        // Assert
        verify(smsService, times(1)).sendCheckIn("BEEF_BRISKET"); // Only the ready one
        verify(repository, times(1)).save(ready); // Should update alertSent=true
    }
}