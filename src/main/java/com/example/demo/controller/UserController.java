package com.example.demo.controller;

import com.example.demo.dto.ChangePinRequest;
import com.example.demo.dto.CreatePinRequest;
import com.example.demo.dto.CreatePinWithPasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-pin")
    public String createPin(@RequestBody CreatePinWithPasswordRequest req,
                            Authentication authentication) {
        LoggerFactory.getLogger(UserController.class).info("Attempting to create transaction PIN for user: {}", authentication.getName());

        if (!req.getPin().matches("\\d{4}")) {
            throw new BadRequestException("PIN must be exactly 4 digits");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTransactionPin() != null) {
            throw new BadRequestException("Transaction PIN already set");
        }


        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid login password");
        }

        user.setTransactionPin(passwordEncoder.encode(req.getPin()));
        userRepository.save(user);
        LoggerFactory.getLogger(UserController.class).info("Transaction PIN created successfully for user: {}", authentication.getName());

        return "Transaction PIN created successfully";
    }

    @GetMapping("/pin-status")
    public boolean isPinSet(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        LoggerFactory.getLogger(UserController.class).info("Checking transaction PIN status for user: {}", authentication.getName());
        return user.getTransactionPin() != null;
    }


    @PostMapping("/change-pin")
    public String changePin(@RequestBody ChangePinRequest req,
                            Authentication auth) {
        LoggerFactory.getLogger(UserController.class).info("Attempting to change transaction PIN for user: {}", auth.getName());

        if (!req.getNewPin().matches("\\d{4}")) {
            throw new BadRequestException("PIN must be exactly 4 digits");
        }

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow();

        if (user.getTransactionPin() == null) {
            throw new BadRequestException("Transaction PIN not set");
        }

        if (!passwordEncoder.matches(req.getOldPin(), user.getTransactionPin())) {
            throw new BadRequestException("Invalid old PIN");
        }

        user.setTransactionPin(passwordEncoder.encode(req.getNewPin()));
        userRepository.save(user);

        LoggerFactory.getLogger(UserController.class).info("Transaction PIN changed successfully for user: {}", auth.getName());

        return "Transaction PIN changed successfully";
    }


}
