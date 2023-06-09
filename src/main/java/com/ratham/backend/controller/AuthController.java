package com.ratham.backend.controller;

import java.util.UUID;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ratham.backend.entity.LoginRequest;
import com.ratham.backend.model.User;
import com.ratham.backend.repository.UserRepository;
import com.ratham.backend.service.TokenService;
import com.ratham.backend.service.UserService;


@RestController
@RequestMapping("/api")
public class AuthController {
   
	@Autowired
	private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/token")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUniversityId(), loginRequest.getPassword())
            );
        } catch(Exception e) {
            return ResponseEntity.ok().body("{\"invalid username/password\"}");
        }

        final UserDetails userDetails = userService
				.loadUserByUsername(loginRequest.getUniversityId());

		final String token = tokenService.generateToken(userDetails);
        
        LOG.info("Token granted {}", token);

        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }


}
