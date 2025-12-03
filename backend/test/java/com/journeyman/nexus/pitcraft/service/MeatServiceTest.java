package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.strategy.MeatPlanFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeatServiceTest {

    @Mock
    private MeatSessionRepository repository;

    @Mock
    private MeatPlanFactory factory;

    @InjectMocks
    private MeatService service;

    @Test
    void createSession_SavesToRepo() {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.CHICKEN);

        CookingPlan plan = CookingPlan.builder()
                .prepTime(LocalDateTime.now())
                .fireTime(LocalDateTime.now().plusHours(12))
                .servingTime(LocalDateTime.now().plusHours(14))
                .build();

        when(factory.generatePlan(any())).thenReturn(plan);
        when(repository.save(any(MeatSession.class))).thenAnswer(i -> i.getArguments()[0]);

        MeatSession result = service.createSession(req);

        assertEquals(CookStatus.PLANNED, result.getStatus());
        verify(repository).save(any(MeatSession.class));
    }

    @Test
    void cancelSession_Success_WhenPlanned() {
        MeatSession session = new MeatSession();
        session.setId("123");
        session.setStatus(CookStatus.PLANNED);

        when(repository.findById("123")).thenReturn(Optional.of(session));

        service.cancelSession("123");

        assertEquals(CookStatus.CANCELLED, session.getStatus());
        verify(repository).save(session);
    }

    @Test
    void cancelSession_ThrowsException_WhenBrining() {
        // "Point of No Return" Test
        MeatSession session = new MeatSession();
        session.setId("999");
        session.setStatus(CookStatus.BRINING); // Too late!

        when(repository.findById("999")).thenReturn(Optional.of(session));

        // Expect Exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            service.cancelSession("999");
        });

        assertTrue(exception.getMessage().contains("Inventory consumed"));

        // Ensure we DID NOT save the cancellation
        verify(repository, never()).save(any());
    }
}