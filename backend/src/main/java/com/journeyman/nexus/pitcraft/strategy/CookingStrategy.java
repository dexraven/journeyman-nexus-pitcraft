package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;

public interface CookingStrategy {

    boolean supports(MeatType meatType);

    // Update this line to match the BaseMeatStrategy
    CookingPlan calculate(MeatRequest request, double outsideTemp);
}