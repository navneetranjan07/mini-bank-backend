package com.example.demo.controller;

import com.example.demo.dto.ChangePinRequest;
import com.example.demo.dto.CreatePinRequest;
import com.example.demo.dto.CreatePinWithPasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-pin")
    public String createPin(@RequestBody CreatePinWithPasswordRequest req,
                            Authentication authentication) {

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

        return "Transaction PIN created successfully";
    }

    @GetMapping("/pin-status")
    public boolean isPinSet(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return user.getTransactionPin() != null;
    }


    @PostMapping("/change-pin")
    public String changePin(@RequestBody ChangePinRequest req,
                            Authentication auth) {

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

        return "Transaction PIN changed successfully";
    }




}
