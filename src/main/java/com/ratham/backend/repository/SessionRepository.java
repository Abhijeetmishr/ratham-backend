package com.ratham.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ratham.backend.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("SELECT s FROM Session s WHERE s.available = true AND DAYOFWEEK(s.startTime) IN (5, 6) AND HOUR(s.startTime) = 10")
    List<Session> findFreeSessionsWithDean();

    @Query("SELECT s FROM Session s WHERE s.available = true AND s.deanId = :deanId AND DAYOFWEEK(s.startTime) IN (5, 6) AND HOUR(s.startTime) = 10")
    List<Session> findFreeSessionsWithDean(@Param("deanId") String deanId);

    @Query("SELECT s FROM Session s WHERE s.available = false AND s.deanId = :deanId AND CURRENT_TIMESTAMP < s.startTime")
    List<Session> findPendingSessionsWithDean(@Param("deanId") String deanId);

    @Query("SELECT s FROM Session s WHERE s.available = false AND s.deanId = :deanId AND :currentDateTime < s.startTime")
    List<Session> findPendingSessionsWithDean(@Param("deanId") String deanId, @Param("currentDateTime") LocalDateTime currentDateTime);


}

