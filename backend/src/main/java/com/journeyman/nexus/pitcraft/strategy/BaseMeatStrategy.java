package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseMeatStrategy implements CookingStrategy {

    protected static final long PREP_HOURS = 12; // Universal Overnight Brine

    @Override
    public CookingPlan calculate(MeatRequest request) {
        ActivePhase phase = calculateActivePhase(request);

        LocalDateTime fireTime = request.getDesiredServingTime()
                .minusHours((long) (phase.cookHours + phase.restHours));

        LocalDateTime prepTime = fireTime.minusHours(PREP_HOURS);

        return CookingPlan.builder()
                .servingTime(request.getDesiredServingTime())
                .prepTime(prepTime)
                .fireTime(fireTime)
                .prepInstructions("Apply Dry Brine / Marinade and refrigerate overnight.")
                .cookInstructions(phase.instructions)
                .totalProcessHours(PREP_HOURS + phase.cookHours + phase.restHours)
                .shoppingList(getIngredients(request))
                .build();
    }

    protected abstract ActivePhase calculateActivePhase(MeatRequest request);
    protected abstract List<String> getIngredients(MeatRequest request);

    @Builder
    protected static class ActivePhase {
        double cookHours;
        double restHours;
        String instructions;
    }
}