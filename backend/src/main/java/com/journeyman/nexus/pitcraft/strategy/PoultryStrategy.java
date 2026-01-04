package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PoultryStrategy extends BaseMeatStrategy {

    @Override
    public boolean supports(MeatType type) {
        return type == MeatType.CHICKEN || type == MeatType.TURKEY;
    }

    // --- LOGIC MOVED HERE ---
    @Override
    protected ActivePhase calculateActivePhase(MeatRequest request, double outsideTemp) {
        // 1. Calculate Base Time (From your old logic)
        // Turkey = 15 mins/lb, Chicken = 20 mins/lb
        double baseMinutes = (request.getType() == MeatType.TURKEY)
                ? (request.getWeightInLbs() * 15)
                : (request.getWeightInLbs() * 20);

        double baseHours = baseMinutes / 60.0;

        // 2. Apply Weather Adjustment (Poultry is sensitive to wind/cold)
        double adjustedHours = baseHours;
        if (outsideTemp < 40.0) {
            adjustedHours = baseHours * 1.15; // +15% if freezing
        } else if (outsideTemp > 90.0) {
            adjustedHours = baseHours * 0.95; // -5% if hot
        }

        return ActivePhase.builder()
                .cookHours(adjustedHours)
                .restHours(request.getType() == MeatType.TURKEY ? 1.0 : 0.5)
                .instructions("Roast High Heat (325F+) until 165F internal.")
                .build();
    }

    @Override
    protected List<String> getIngredients(MeatRequest request) {
        return List.of(request.getType().toString(), "Poultry Rub", "Butter", "Fresh Herbs");
    }
}