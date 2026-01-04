package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.TemperatureLog;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.repository.TemperatureLogRepository;
import com.journeyman.nexus.pitcraft.strategy.MeatPlanFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeatService {

    private final MeatSessionRepository sessionRepository;
    private final TemperatureLogRepository tempRepo;
    private final SmsService smsService;
    private final MeatPlanFactory factory;
    private final WeatherService weatherService; // <--- 1. NEW DEPENDENCY

    public MeatSession createSession(MeatRequest request) {
        // 2. Get Real Weather (Defaults to 70F if API fails)
        double outsideTemp = weatherService.getCurrentTempFahrenheit();

        // 3. Generate Plan (Pass the temp so Strategy can adjust hours!)
        CookingPlan plan = factory.generatePlan(request, outsideTemp);

        MeatSession session = MeatSession.builder()
                .meatType(request.getType())
                .weightInLbs(request.getWeightInLbs())
                .prepTime(plan.getPrepTime())
                .fireTime(plan.getFireTime())
                .servingTime(plan.getServingTime())
                .prepInstructions(plan.getPrepInstructions())
                .cookInstructions(plan.getCookInstructions())
                .status(CookStatus.PLANNED)
                .alertSent(false)
                .build();

        return sessionRepository.save(session);
    }

    public void cancelSession(String id) {
        MeatSession session = getSession(id); // Use the helper here too for consistency

        // THE RULE: Point of No Return
        if (session.getStatus() != CookStatus.PLANNED) {
            throw new IllegalStateException(
                    "CANCELLATION REJECTED: Meat is already in " + session.getStatus() +
                            " phase. Inventory consumed. No Refund."
            );
        }

        session.setStatus(CookStatus.CANCELLED);
        sessionRepository.save(session);
    }

    public void logTemperature(String sessionId, double currentTemp) {
        MeatSession session = getSession(sessionId);

        // 1. Save the new log
        TemperatureLog log = new TemperatureLog(session, currentTemp);
        tempRepo.save(log);

        // 2. Check for Stall (Only if cooking and not already alerted)
        if (session.getStatus() == CookStatus.COOKING && !session.isAlertSent()) {
            checkForStall(session, currentTemp);
        }
    }

    public List<MeatSession> getActiveSessions() {
        return sessionRepository.findByStatusNot(CookStatus.SERVED);
    }

    private void checkForStall(MeatSession session, double currentTemp) {
        // Stall usually happens between 150F and 170F
        if (currentTemp < 150 || currentTemp > 180) return;

        // Get logs from the last 45 minutes
        LocalDateTime cutOff = LocalDateTime.now().minusMinutes(45);
        List<TemperatureLog> history = tempRepo.findRecentLogs(session.getId(), cutOff);

        if (history.isEmpty()) return;

        double oldestTemp = history.get(0).getDegreesFahrenheit();
        double diff = currentTemp - oldestTemp;

        // THE RULE: If temp rose less than 2 degrees in 45 mins -> STALL DETECTED
        if (diff < 2.0) {
            smsService.sendStallAlert(session.getMeatType().toString(), currentTemp);

            // Mark alert as sent so we don't spam the user every 5 mins
            session.setAlertSent(true);
            sessionRepository.save(session);
        }
    }

    private MeatSession getSession(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));
    }
}