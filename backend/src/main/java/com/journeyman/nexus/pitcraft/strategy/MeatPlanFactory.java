package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeatPlanFactory {

    private final List<CookingStrategy> strategies;

    public MeatPlanFactory(List<CookingStrategy> strategies) {
        this.strategies = strategies;
    }

    // UPDATE: Now accepts 'outsideTemp'
    public CookingPlan generatePlan(MeatRequest request, double outsideTemp) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(request.getType()))
                .findFirst()
                // UPDATE: Passes 'outsideTemp' to the strategy
                .map(strategy -> strategy.calculate(request, outsideTemp))
                .orElseThrow(() -> new IllegalArgumentException("No strategy for: " + request.getType()));
    }
}