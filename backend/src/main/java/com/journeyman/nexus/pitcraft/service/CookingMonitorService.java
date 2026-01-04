package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CookingMonitorService {

    private final MeatSessionRepository repository;
    private final SmsService smsService;

    @Scheduled(fixedRate = 60000) // Every minute
    public void checkMeatStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<MeatSession> activeMeats = repository.findByStatus(CookStatus.COOKING);

        for (MeatSession meat : activeMeats) {
            // Alert 15 mins before serving time
            if (now.isAfter(meat.getServingTime().minusMinutes(15)) && !meat.isAlertSent()) {
                smsService.sendCheckIn(meat.getMeatType().toString());
                meat.setAlertSent(true);
                repository.save(meat);
            }
        }
    }
}