package com.journeyman.nexus.pitcraft.dto;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MeatRequestTest {

    @Test
    void testGettersAndSetters() {
        // 1. Create instance
        MeatRequest request = new MeatRequest();
        LocalDateTime now = LocalDateTime.now();

        // 2. Use Setters
        request.setType(MeatType.BEEF_BRISKET);
        request.setWeightInLbs(12.5);
        request.setDesiredServingTime(now);

        // 3. Use Getters and Assert
        assertEquals(MeatType.BEEF_BRISKET, request.getType());
        assertEquals(12.5, request.getWeightInLbs());
        assertEquals(now, request.getDesiredServingTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime time = LocalDateTime.of(2025, 1, 1, 12, 0);

        // Object 1
        MeatRequest req1 = new MeatRequest();
        req1.setType(MeatType.PORK_RIBS);
        req1.setWeightInLbs(5.0);
        req1.setDesiredServingTime(time);

        // Object 2 (Identical to 1)
        MeatRequest req2 = new MeatRequest();
        req2.setType(MeatType.PORK_RIBS);
        req2.setWeightInLbs(5.0);
        req2.setDesiredServingTime(time);

        // Object 3 (Different)
        MeatRequest req3 = new MeatRequest();
        req3.setType(MeatType.CHICKEN);

        // Assert Equals
        assertEquals(req1, req2);
        assertNotEquals(req1, req3);

        // Assert HashCode (Equal objects must have equal hash codes)
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1.hashCode(), req3.hashCode());
    }

    @Test
    void testToString() {
        MeatRequest request = new MeatRequest();
        request.setType(MeatType.BEEF_BRISKET);
        request.setWeightInLbs(10.0);

        String result = request.toString();

        // Lombok @Data generates a toString that includes the class name and field values
        assertNotNull(result);
        assertTrue(result.contains("MeatRequest"));
        assertTrue(result.contains("BRISKET"));
        assertTrue(result.contains("10.0"));
    }

    @Test
    void testCanEqual() {
        // Lombok generates a canEqual method for inheritance protection
        MeatRequest req1 = new MeatRequest();
        MeatRequest req2 = new MeatRequest();
        assertTrue(req1.canEqual(req2));
    }
}