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

    @Override
    protected ActivePhase calculateActivePhase(MeatRequest request) {
        double cookTime = switch (request.getType()) {
            case PORK_SHOULDER, PORK_RIBS, BEEF_RIBS -> 7.0;
            case LAMB -> 6.0;
            case PORK_BELLY -> 4.0;
            default -> 6.0;
        };

        return ActivePhase.builder()
                .cookHours(cookTime)
                .restHours(1.0)
                .instructions("Smoke at 225F. Wrap when bark is set.")
                .build();
    }

    @Override
    public List<String> getIngredients(MeatRequest request) {
        return switch (request.getType()) {
            case BEEF_BRISKET, BEEF_RIBS -> List.of("Wagyu Tallow", "Head Country", "Brisket Magic", "Salt Lick");
            case PORK_SHOULDER, PORK_RIBS -> List.of("Binder","Ribnoxious", "Honey Hog", "Apple Cider Vinegar"); // Fixes the test
            case CHICKEN -> List.of("Cluckalicious", "Cajun");
            default -> List.of("Salt", "Pepper");
        };
    }
}