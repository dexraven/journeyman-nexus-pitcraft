package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;

public interface CookingStrategy {
    boolean supports(MeatType type);
    CookingPlan calculate(MeatRequest request);
}