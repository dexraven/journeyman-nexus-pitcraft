package com.journeyman.nexus.pitcraft.repository;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.domain.MeatType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class MeatSessionRepositoryTest {

    @Autowired
    private MeatSessionRepository repository;

    @Test
    void testFindByStatus() {
        // 1. Create and Save a Session
        MeatSession session = new MeatSession();
        session.setId(UUID.randomUUID().toString());
        session.setMeatType(MeatType.BEEF_BRISKET);
        session.setStatus(CookStatus.COOKING);
        session.setFireTime(LocalDateTime.now());
        session.setServingTime(LocalDateTime.now().plusHours(12));
        session.setWeightInLbs(12.0);

        repository.save(session);

        // 2. Query by Status
        List<MeatSession> results = repository.findByStatus(CookStatus.COOKING);

        // 3. Assert
        assertFalse(results.isEmpty());
        assertEquals(MeatType.BEEF_BRISKET, results.getFirst().getMeatType());
    }

    @Test
    void testSaveAndRetrieve() {
        MeatSession session = new MeatSession();
        session.setId("test-id");
        session.setMeatType(MeatType.PORK_SHOULDER);
        session.setStatus(CookStatus.PLANNED);

        MeatSession savedSession = repository.save(session);

        MeatSession found = repository.findById(savedSession.getId()).orElseThrow();

        assertEquals(CookStatus.PLANNED, found.getStatus());
    }
}