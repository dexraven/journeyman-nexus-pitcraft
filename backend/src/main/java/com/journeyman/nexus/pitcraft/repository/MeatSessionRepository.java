package com.journeyman.nexus.pitcraft.repository;

import com.journeyman.nexus.pitcraft.domain.CookStatus;
import com.journeyman.nexus.pitcraft.domain.MeatSession;
import org.springframework.data.jpa.repository.JpaRepository; // <--- MUST HAVE THIS
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeatSessionRepository extends JpaRepository<MeatSession, String> { // <--- MUST EXTEND THIS

    // Custom methods
    List<MeatSession> findByStatus(CookStatus status);
    List<MeatSession> findByStatusNot(CookStatus status);
}