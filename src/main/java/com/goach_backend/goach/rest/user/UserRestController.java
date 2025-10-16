package com.goach_backend.goach.rest.user;

import com.goach_backend.goach.logic.entity.email.EmailService;
import com.goach_backend.goach.logic.entity.role.RoleEnum;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.jar.Attributes;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public User addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/filterByName/{name}")
    public List<User> getUserById(@PathVariable String name) {
        return userRepository.findUsersWithCharacterInName(name);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User user) {
        Optional<User> auxUser = userRepository.findById(id);
        if (auxUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User currentUser = authenticatedUser();

        if (currentUser.getRole() != RoleEnum.ADMIN &&
                !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only update your own profile"));
        }

        User existingUser = auxUser.get();

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        User savedUser = userRepository.save(existingUser);

        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/email/{id}")
    public String sendEmailUser (@PathVariable UUID id, @RequestBody User user) throws IOException {
        Random random = new Random();
        int min = 100000;
        int max = 999999;

        int privateCode = random.nextInt(max - min + 1) + min;


        userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setPrivateCode(privateCode);

                    return userRepository.save(existingUser);

                })
                .orElseGet(() -> {
                    user.setId(id);
                    return userRepository.save(user);
                });

        return emailService.sendTextEmail(user.getEmail(), privateCode);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserPassword(@PathVariable UUID id, @RequestBody User user) {
        Optional<User> auxUser = userRepository.findById(id);
        if (auxUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User currentUser = authenticatedUser();

        if (currentUser.getRole() != RoleEnum.ADMIN &&
                !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only update your own profile"));
        }

        if (!Objects.equals(user.getPrivateCode(), currentUser.getPrivateCode())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "The provided private codes don't match"));
        }

        User existingUser = auxUser.get();

        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(existingUser);

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}
