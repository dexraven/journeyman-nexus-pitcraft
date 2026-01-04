package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.repository.TemperatureLogRepository;
import com.journeyman.nexus.pitcraft.strategy.MeatPlanFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeatServiceTest {

    @Mock private MeatSessionRepository sessionRepo;
    @Mock private TemperatureLogRepository tempRepo;
    @Mock private SmsService smsService;
    @Mock private MeatPlanFactory planFactory;
    @Mock private WeatherService weatherService; // <--- 1. NEW MOCK

    @InjectMocks
    private MeatService meatService;

    @Test
    void createSession_UsesWeatherAndFactory() {
        // 1. Arrange
        MeatRequest request = new MeatRequest();
        request.setType(MeatType.BEEF_BRISKET);
        request.setWeightInLbs(12.0);

        // Mock Weather: It's a cold day!
        when(weatherService.getCurrentTempFahrenheit()).thenReturn(35.0);

        // Mock Plan: Factory returns a dummy plan
        CookingPlan plan = CookingPlan.builder()
                .prepTime(LocalDateTime.now())
                .fireTime(LocalDateTime.now().plusHours(1))
                .servingTime(LocalDateTime.now().plusHours(14))
                .prepInstructions("Rub it")
                .cookInstructions("Smoke it")
                .build();

        // Expect the factory to be called with the Cold Temp (35.0)
        when(planFactory.generatePlan(eq(request), eq(35.0))).thenReturn(plan);

        // Mock Repo Save
        when(sessionRepo.save(any(MeatSession.class))).thenAnswer(i -> {
            MeatSession s = i.getArgument(0);
            s.setId("saved-id");
            return s;
        });

        // 2. Act
        MeatSession result = meatService.createSession(request);

        // 3. Assert
        assertNotNull(result);
        assertEquals("saved-id", result.getId());
        assertEquals(CookStatus.PLANNED, result.getStatus());

        // 4. Verify we actually checked the weather
        verify(weatherService).getCurrentTempFahrenheit();
        verify(planFactory).generatePlan(request, 35.0);
    }

    @Test
    void cancelSession_Success() {
        // Arrange
        MeatSession session = new MeatSession();
        session.setId("123");
        session.setStatus(CookStatus.PLANNED); // Can only cancel if PLANNED

        when(sessionRepo.findById("123")).thenReturn(Optional.of(session));

        // Act
        meatService.cancelSession("123");

        // Assert
        assertEquals(CookStatus.CANCELLED, session.getStatus());
        verify(sessionRepo).save(session);
    }

    @Test
    void cancelSession_TooLate_ThrowsException() {
        // Arrange
        MeatSession session = new MeatSession();
        session.setId("123");
        session.setStatus(CookStatus.COOKING); // Too late!

        when(sessionRepo.findById("123")).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> meatService.cancelSession("123"));

        // Ensure we didn't save the status change
        verify(sessionRepo, never()).save(any());
    }

    @Test
    void cancelSession_NotFound_ThrowsException() {
        when(sessionRepo.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meatService.cancelSession("unknown"));
    }
}