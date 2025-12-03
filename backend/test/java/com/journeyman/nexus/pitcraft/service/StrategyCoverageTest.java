package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrategyCoverageTest {

    private final LocalDateTime SERVING = LocalDateTime.now().plusDays(2);

    @Test
    void testSlowSmokeStrategy() {
        SlowSmokeStrategy s = new SlowSmokeStrategy();
        assertTrue(s.supports(MeatType.BEEF_BRISKET));
        assertFalse(s.supports(MeatType.CHICKEN));

        MeatRequest req = new MeatRequest();
        req.setType(MeatType.BEEF_BRISKET);
        req.setDesiredServingTime(SERVING);
        req.setWeightInLbs(12.0);

        CookingPlan plan = s.calculate(req);
        assertEquals(36.0, plan.getTotalProcessHours()); // 12 prep + 12 cook + 12 rest
        assertTrue(plan.getShoppingList().contains("Pink Butcher Paper"));
    }

    @Test
    void testStandardSmokeStrategy_AllCases() {
        StandardSmokeStrategy s = new StandardSmokeStrategy();

        // Test PORK_SHOULDER
        CookingPlan p1 = s.calculate(req(MeatType.PORK_SHOULDER));
        assertEquals(7.0 + 1.0 + 12.0, p1.getTotalProcessHours());

        // Test LAMB
        CookingPlan p2 = s.calculate(req(MeatType.LAMB));
        // 6h cook + 1h rest + 12h prep
        assertEquals(19.0, p2.getTotalProcessHours());

        // Test BELLY
        CookingPlan p3 = s.calculate(req(MeatType.PORK_BELLY));
        // 4h cook + 1h rest + 12h prep
        assertEquals(17.0, p3.getTotalProcessHours());

        // Test RIBS (Fall through logic)
        CookingPlan p4 = s.calculate(req(MeatType.PORK_RIBS));
        assertEquals(20.0, p4.getTotalProcessHours());

        // Test Ingredients logic branching
        assertTrue(s.getIngredients(req(MeatType.PORK_SHOULDER)).contains("Pork Rub"));
        assertTrue(s.getIngredients(req(MeatType.BEEF_RIBS)).contains("BBQ Sauce"));
    }

    @Test
    void testPoultryStrategy_BothBirds() {
        PoultryStrategy s = new PoultryStrategy();

        // Turkey (15min/lb)
        MeatRequest turkey = req(MeatType.TURKEY);
        turkey.setWeightInLbs(10.0); // 150 mins = 2.5 hrs
        CookingPlan p1 = s.calculate(turkey);
        // 2.5 cook + 1.0 rest + 12 prep = 15.5
        assertEquals(15.5, p1.getTotalProcessHours());

        // Chicken (20min/lb)
        MeatRequest chicken = req(MeatType.CHICKEN);
        chicken.setWeightInLbs(6.0); // 120 mins = 2.0 hrs
        CookingPlan p2 = s.calculate(chicken);
        // 2.0 cook + 0.5 rest + 12 prep = 14.5
        assertEquals(14.5, p2.getTotalProcessHours());
    }

    @Test
    void testFactory_Exceptions() {
        MeatPlanFactory factory = new MeatPlanFactory(List.of(new SlowSmokeStrategy()));
        MeatRequest req = req(MeatType.CHICKEN); // Not supported by SlowSmoke

        assertThrows(IllegalArgumentException.class, () -> factory.generatePlan(req));
    }

    private MeatRequest req(MeatType type) {
        MeatRequest r = new MeatRequest();
        r.setType(type);
        r.setDesiredServingTime(SERVING);
        r.setWeightInLbs(10.0);
        return r;
    }
}