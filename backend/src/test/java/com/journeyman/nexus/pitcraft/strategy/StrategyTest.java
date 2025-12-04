package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrategyTest {

    private final LocalDateTime SERVING = LocalDateTime.of(2025, 12, 25, 18, 0);

    @Test
    void testBrisket_36HourProtocol() {
        SlowSmokeStrategy strategy = new SlowSmokeStrategy();
        MeatRequest req = createRequest(MeatType.BEEF_BRISKET, 12.0);

        CookingPlan plan = strategy.calculate(req);

        // 12h Smoke + 12h Rest + 12h Prep (Base) = 36 Hours Total
        assertEquals(36.0, plan.getTotalProcessHours());

        // Prep should start 36 hours before serving (Dec 24, 06:00)
        assertEquals(SERVING.minusHours(36), plan.getPrepTime());

        // Verify Shopping List
        assertTrue(plan.getShoppingList().contains("Pink Butcher Paper"));
    }

    @Test
    void testRibs_StandardSmoke() {
        StandardSmokeStrategy strategy = new StandardSmokeStrategy();
        MeatRequest req = createRequest(MeatType.PORK_RIBS, 0.0); // Weight irrelevant for ribs logic

        CookingPlan plan = strategy.calculate(req);

        // 7h Cook + 1h Rest + 12h Prep = 20 Hours Total
        assertEquals(20.0, plan.getTotalProcessHours());
        assertEquals(7.0, plan.getFireTime().until(SERVING.minusHours(1), java.time.temporal.ChronoUnit.HOURS));
    }

    @Test
    void testTurkey_HighHeat() {
        PoultryStrategy strategy = new PoultryStrategy();
        MeatRequest req = createRequest(MeatType.TURKEY, 10.0); // 10 lbs

        CookingPlan plan = strategy.calculate(req);

        // 10lbs * 15min = 150min (2.5 hrs)
        // Cook 2.5 + Rest 1.0 + Prep 12 = 15.5 Total
        assertEquals(15.5, plan.getTotalProcessHours());
    }

    @Test
    void testFactory_Routing() {
        MeatPlanFactory factory = new MeatPlanFactory(List.of(
                new SlowSmokeStrategy(),
                new PoultryStrategy()
        ));

        MeatRequest req = createRequest(MeatType.BEEF_BRISKET, 12.0);
        CookingPlan plan = factory.generatePlan(req);

        assertNotNull(plan);
        assertTrue(plan.getShoppingList().contains("Pink Butcher Paper"), "Factory should pick Brisket strategy");
    }

    private MeatRequest createRequest(MeatType type, double weight) {
        MeatRequest req = new MeatRequest();
        req.setType(type);
        req.setWeightInLbs(weight);
        req.setDesiredServingTime(SERVING);
        return req;
    }
}