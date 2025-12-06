package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoultryStrategyTest {

    private final PoultryStrategy strategy = new PoultryStrategy();

    @Test
    void calculate_Turkey_WeatherLogic() {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.TURKEY);
        req.setWeightInLbs(10.0); // 10lbs * 15min = 150min = 2.5 hours
        req.setDesiredServingTime(LocalDateTime.now());

        // Base Logic: 12h Prep + 2.5h Cook + 1h Rest

        // Case A: Normal (70F) -> Total 15.5
        CookingPlan normal = strategy.calculate(req, 70.0);
        assertEquals(15.5, normal.getTotalProcessHours(), 0.01);

        // Case B: Cold (30F) -> Cook * 1.15 (2.5 * 1.15 = 2.875)
        // Total = 12 + 2.875 + 1 = 15.875
        CookingPlan cold = strategy.calculate(req, 30.0);
        assertEquals(15.875, cold.getTotalProcessHours(), 0.01);
    }

    @Test
    void calculate_Chicken_Logic() {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.CHICKEN);
        req.setWeightInLbs(6.0); // 6lbs * 20min = 120min = 2.0 hours
        req.setDesiredServingTime(LocalDateTime.now());

        // Chicken Rest is 0.5 hours
        // Total = 12h Prep + 2.0h Cook + 0.5h Rest = 14.5
        CookingPlan plan = strategy.calculate(req, 70.0);
        assertEquals(14.5, plan.getTotalProcessHours(), 0.01);
    }
}