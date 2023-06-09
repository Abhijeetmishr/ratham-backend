package com.ratham.backend.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ratham.backend.model.Session;
import com.ratham.backend.repository.SessionRepository;
import com.ratham.backend.service.TokenService;
import com.ratham.backend.service.UserService;

@RestController
@RequestMapping("/api")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TokenService tokenService;

    private static final Logger LOG = LoggerFactory.getLogger(SessionController.class);

    @GetMapping("/availableSessions")
    public ResponseEntity<List<Session>> getAllFreeSessions() {
        // Query the data source to fetch the list of available sessions with the dean
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Session> sessions = sessionRepository.findFreeSessionsWithDean();
        List<Session> availableSessions = sessions.stream()
                .filter(session -> currentDateTime.isAfter(session.getStartTime()) && session.isAvailable())
                .collect(Collectors.toList());      
        LOG.info("Sessions: ", availableSessions);
        return ResponseEntity.ok().body(sessions);
    }

    @PostMapping("/bookSession/{deanId}")
    public ResponseEntity<List<Session>> getFreeSessionsWithDean(@PathVariable("deanId") String deanId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        LOG.info("UserName: {}", username);
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Session> sessions = sessionRepository.findFreeSessionsWithDean(deanId);

        boolean sessionBooked = sessions.stream()
            .filter(session -> session.getStartTime().isAfter(currentDateTime) && session.isAvailable())
            .findFirst()
            .map(session -> {
                session.setAvailable(false);
                session.setBookedBy(username);
                sessionRepository.save(session);
                return true; // session booked successfully
            })
            .orElse(false); // no available sessions or booking failed
        
        if (sessionBooked) {
            LOG.info("Session booked successfully");
            return ResponseEntity.ok().body(sessions);
        } else {
            LOG.warn("No available sessions or booking failed");
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/pendingSessions")
    public ResponseEntity<List<Session>> bookSession() {
        // Query the data source to fetch the list of available sessions with the dean

        List<Session> sessions = sessionRepository.findFreeSessionsWithDean();
        
        LOG.info("Sessions: ", sessions);
        return ResponseEntity.ok().body(sessions);
    }
}
