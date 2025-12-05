package com.journeyman.nexus.pitcraft.repository;

import com.journeyman.nexus.pitcraft.domain.TemperatureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TemperatureLogRepository extends JpaRepository<TemperatureLog, String> {

    @Query("SELECT t FROM TemperatureLog t WHERE t.meatSession.id = :sessionId AND t.logTime > :startTime ORDER BY t.logTime ASC")
    List<TemperatureLog> findRecentLogs(@Param("sessionId") String sessionId, @Param("startTime") LocalDateTime startTime);
}