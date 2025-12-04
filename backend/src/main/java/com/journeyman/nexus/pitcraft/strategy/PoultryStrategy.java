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

    @Override
    protected ActivePhase calculateActivePhase(MeatRequest request) {
        double cookTime = (request.getType() == MeatType.TURKEY)
                ? (request.getWeightInLbs() * 15) / 60.0
                : (request.getWeightInLbs() * 20) / 60.0;

        return ActivePhase.builder()
                .cookHours(cookTime)
                .restHours(request.getType() == MeatType.TURKEY ? 1.0 : 0.5)
                .instructions("Roast High Heat until 165F internal.")
                .build();
    }

    @Override
    protected List<String> getIngredients(MeatRequest request) {
        return List.of(request.getType().toString(), "Poultry Rub", "Butter", "Fresh Herbs");
    }
}