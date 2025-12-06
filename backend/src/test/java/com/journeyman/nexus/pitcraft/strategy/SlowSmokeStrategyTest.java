package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlowSmokeStrategyTest {

    private final SlowSmokeStrategy strategy = new SlowSmokeStrategy();

    @Test
    void calculate_WeatherAdjustments() {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.BEEF_BRISKET);
        req.setWeightInLbs(12.0); // Weight is ignored for time in this strategy
        req.setDesiredServingTime(LocalDateTime.now());

        // Base Logic: 12h Prep + 12h Cook + 12h Rest = 36 Total

        // Case A: Normal (70F) -> No Change
        CookingPlan normal = strategy.calculate(req, 70.0);
        assertEquals(36.0, normal.getTotalProcessHours());

        // Case B: Cold (30F) -> Cook increases 15% (12 * 1.15 = 13.8h)
        // Total = 12(Prep) + 13.8(Cook) + 12(Rest) = 37.8
        CookingPlan cold = strategy.calculate(req, 30.0);
        assertEquals(37.8, cold.getTotalProcessHours(), 0.01);

        // Case C: Hot (100F) -> Cook decreases 5% (12 * 0.95 = 11.4h)
        // Total = 12(Prep) + 11.4(Cook) + 12(Rest) = 35.4
        CookingPlan hot = strategy.calculate(req, 100.0);
        assertEquals(35.4, hot.getTotalProcessHours(), 0.01);
    }
}