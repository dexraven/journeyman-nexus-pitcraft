package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.repository.MeatSessionRepository;
import com.journeyman.nexus.pitcraft.strategy.MeatPlanFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeatService {

    private final MeatSessionRepository repository;
    private final MeatPlanFactory factory;

    public MeatSession createSession(MeatRequest request) {
        CookingPlan plan = factory.generatePlan(request);

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

        return repository.save(session);
    }

    public void cancelSession(String id) {
        MeatSession session = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + id));

        // THE RULE: Point of No Return
        if (session.getStatus() != CookStatus.PLANNED) {
            throw new IllegalStateException(
                    "CANCELLATION REJECTED: Meat is already in " + session.getStatus() +
                            " phase. Inventory consumed. No Refund."
            );
        }

        session.setStatus(CookStatus.CANCELLED);
        repository.save(session);
    }

    public List<MeatSession> getActiveSessions() {
        return repository.findByStatusNot(CookStatus.SERVED);
    }
}