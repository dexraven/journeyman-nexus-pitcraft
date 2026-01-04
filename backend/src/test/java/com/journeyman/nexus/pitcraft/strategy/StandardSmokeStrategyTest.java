package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardSmokeStrategyTest {

    private final StandardSmokeStrategy strategy = new StandardSmokeStrategy();

    @Test
    void calculate_PorkShoulder_WeatherLogic() {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.PORK_SHOULDER);
        req.setDesiredServingTime(LocalDateTime.now());

        // Base Logic: 12h Prep + 7.0h Cook + 1.0h Rest = 20.0 Total

        // Case A: Normal
        CookingPlan normal = strategy.calculate(req, 70.0);
        assertEquals(20.0, normal.getTotalProcessHours());

        // Case B: Hot (100F) -> Cook * 0.95 (7.0 * 0.95 = 6.65)
        // Total = 12 + 6.65 + 1 = 19.65
        CookingPlan hot = strategy.calculate(req, 100.0);
        assertEquals(19.65, hot.getTotalProcessHours(), 0.01);
    }

    @Test
    void calculate_Lamb_Logic() {
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.LAMB);
        req.setDesiredServingTime(LocalDateTime.now());

        // Lamb Base Cook = 6.0 hours
        // Total = 12 + 6.0 + 1 = 19.0
        CookingPlan plan = strategy.calculate(req, 70.0);
        assertEquals(19.0, plan.getTotalProcessHours());
    }
}