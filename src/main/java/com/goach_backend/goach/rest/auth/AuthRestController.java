package com.goach_backend.goach.rest.auth;

import com.goach_backend.goach.logic.entity.auth.AuthenticationService;
import com.goach_backend.goach.logic.entity.auth.JwtService;
import com.goach_backend.goach.logic.entity.email.EmailService;
import com.goach_backend.goach.logic.entity.role.RoleEnum;
import com.goach_backend.goach.logic.entity.user.LoginResponse;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RequestMapping("/auth")
@RestController
public class AuthRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthRestController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody User user) {
        User authenticatedUser = authenticationService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime() / 1000);

        Optional<User> foundedUser = userRepository.findByEmail(user.getEmail());

        foundedUser.ifPresent(loginResponse::setAuthUser);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        RoleEnum role = RoleEnum.valueOf(user.getRole().toString().toUpperCase());

        user.setRole(role);
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/emailCode")
    public String sendEmailUser(@RequestBody User user) throws IOException {
        Random random = new Random();
        int min = 100000;
        int max = 999999;

        int privateCode = random.nextInt(max - min + 1) + min;


        userRepository.findByEmail(user.getEmail())
                .map(existingUser -> {
                    existingUser.setPrivateCode(privateCode);

                    return userRepository.save(existingUser);

                })
                .orElseGet(() -> {
                    user.setEmail(user.getEmail());
                    return userRepository.save(user);
                });

        return emailService.sendTextEmail(user.getEmail(), privateCode);
    }

    @PutMapping("/passwordChange")
    public ResponseEntity<?> updateUserPassword(@RequestBody User user) {
        Optional<User> auxUser = userRepository.findByEmail(user.getEmail());
        if (auxUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        if (!Objects.equals(user.getPrivateCode(), auxUser.get().getPrivateCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "The provided private codes don't match"));
        }

        User existingUser = auxUser.get();

        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(existingUser);

        return ResponseEntity.ok(savedUser);
    }
}
