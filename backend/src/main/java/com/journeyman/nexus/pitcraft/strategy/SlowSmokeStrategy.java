package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlowSmokeStrategy extends BaseMeatStrategy {

    @Override
    public boolean supports(MeatType type) {
        return type == MeatType.BEEF_BRISKET;
    }

    // --- UPDATED METHOD SIGNATURE ---
    @Override
    protected ActivePhase calculateActivePhase(MeatRequest request, double outsideTemp) {
        // Base: 12 hours cook (Goldee's Method style)
        double baseCookHours = 12.0;

        // Apply Weather Adjustment to the COOK phase only
        double adjustedCookHours = baseCookHours;
        if (outsideTemp < 40.0) {
            adjustedCookHours = baseCookHours * 1.15; // +15% (Approx 13.8 hours)
        } else if (outsideTemp > 90.0) {
            adjustedCookHours = baseCookHours * 0.95; // -5% (Approx 11.4 hours)
        }

        // Rest is usually indoors/controlled, so we don't adjust it for weather
        double restHours = 12.0;

        return ActivePhase.builder()
                .cookHours(adjustedCookHours)
                .restHours(restHours)
                .instructions("Smoke until probe tender (~203F), then Heated Rest for 12 hours.")
                .build();
    }

    @Override
    protected List<String> getIngredients(MeatRequest request) {
        return List.of(
                "Full Packer Brisket", "Coarse Black Pepper (16 mesh)",
                "Kosher Salt", "Pink Butcher Paper", "Post Oak Wood Splits"
        );
    }
}