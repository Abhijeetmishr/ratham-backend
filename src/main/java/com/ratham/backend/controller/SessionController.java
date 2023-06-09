package com.ratham.backend.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/sessions")
    public ResponseEntity<List<Session>> getAllFreeSessions() {
        // Query the data source to fetch the list of available sessions with the dean

        List<Session> sessions = sessionRepository.findFreeSessionsWithDean();
        
        LOG.info("Sessions: ", sessions);
        return ResponseEntity.ok().body(sessions);
    }

    @GetMapping("/bookSession/{deanId}")
    public ResponseEntity<List<Session>> getFreeSessionsWithDean(@PathVariable("deanId") String deanId,  HttpServletRequest request) {
        // Query the data source to fetch the list of available sessions with the dean
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = tokenService.extractUsername(jwt);
        }

        List<Session> sessions = sessionRepository.findFreeSessionsWithDean(deanId);
        LocalDateTime currentDateTime = LocalDateTime.now();
        for(Session session: sessions) {
            if(session.getStartTime().isAfter(currentDateTime) && session.isAvailable()) {
                session.setAvailable(false); 
                session.setBookedBy(username);
                sessionRepository.save(session);
                break;
            }
        }
        LOG.info("Sessions: ", sessions);
        return ResponseEntity.ok().body(sessions);
    }

    @GetMapping("/pendingSessions")
    public ResponseEntity<List<Session>> bookSession() {
        // Query the data source to fetch the list of available sessions with the dean

        List<Session> sessions = sessionRepository.findFreeSessionsWithDean();
        
        LOG.info("Sessions: ", sessions);
        return ResponseEntity.ok().body(sessions);
    }
}
