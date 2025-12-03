package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class StandardSmokeStrategy extends BaseMeatStrategy {

    @Override
    public boolean supports(MeatType type) {
        return type == MeatType.PORK_SHOULDER || type == MeatType.BEEF_RIBS ||
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
    protected List<String> getIngredients(MeatRequest request) {
        return List.of(request.getType().toString(), "Apple Cider Vinegar", "Fruit Wood Chunks");
    }
}