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

    public CookingPlan generatePlan(MeatRequest request) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(request.getType()))
                .findFirst()
                .map(strategy -> strategy.calculate(request))
                .orElseThrow(() -> new IllegalArgumentException("No strategy for: " + request.getType()));
    }
}