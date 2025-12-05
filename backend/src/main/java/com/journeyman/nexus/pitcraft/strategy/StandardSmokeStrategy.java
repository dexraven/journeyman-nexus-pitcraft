package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.journeyman.nexus.pitcraft.domain.MeatType.BEEF_RIBS;
import static com.journeyman.nexus.pitcraft.domain.MeatType.PORK_SHOULDER;

@Component
public class StandardSmokeStrategy extends BaseMeatStrategy {

    @Override
    public boolean supports(MeatType type) {
        return type == PORK_SHOULDER || type == BEEF_RIBS ||
                type == MeatType.PORK_RIBS || type == MeatType.PORK_BELLY || type == MeatType.LAMB;
    }

    // --- UPDATED METHOD SIGNATURE & LOGIC ---
    @Override
    protected ActivePhase calculateActivePhase(MeatRequest request, double outsideTemp) {
        // 1. Determine Base Hours
        double baseHours = switch (request.getType()) {
            case PORK_SHOULDER, PORK_RIBS, BEEF_RIBS -> 7.0;
            case LAMB -> 6.0;
            case PORK_BELLY -> 4.0;
            default -> 6.0;
        };

        // 2. Apply Weather Adjustment
        // If it's cold (< 40F), adds 15% time. If hot (> 90F), removes 5%.
        double adjustedHours = applyWeatherFactor(baseHours, outsideTemp);

        return ActivePhase.builder()
                .cookHours(adjustedHours)
                .restHours(1.0)
                .instructions("Smoke at 225F. Wrap when bark is set.")
                .build();
    }

    @Override
    public List<String> getIngredients(MeatRequest request) {
        return switch (request.getType()) {
            case BEEF_BRISKET, BEEF_RIBS -> List.of("Wagyu Tallow", "Head Country", "Brisket Magic", "Salt Lick");
            case PORK_SHOULDER, PORK_RIBS -> List.of("Binder","Ribnoxious", "Honey Hog", "Apple Cider Vinegar");
            case CHICKEN -> List.of("Cluckalicious", "Cajun");
            default -> List.of("Salt", "Pepper");
        };
    }

    // Helper method to keep the math clean
    private double applyWeatherFactor(double hours, double temp) {
        if (temp < 40.0) {
            return hours * 1.15; // +15%
        } else if (temp > 90.0) {
            return hours * 0.95; // -5%
        }
        return hours;
    }
}