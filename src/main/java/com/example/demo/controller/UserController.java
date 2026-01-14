package com.example.demo.controller;

import com.example.demo.dto.CreatePinRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public String createPin(@RequestBody CreatePinRequest req,
                            Authentication authentication) {

        if (!req.getPin().matches("\\d{4}")) {
            return "PIN must be exactly 4 digits";
        }

        String email = authentication.getName(); // ✅ FIXED

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTransactionPin() != null) {
            return "PIN already set";
        }

        user.setTransactionPin(
                passwordEncoder.encode(req.getPin())
        );

        userRepository.save(user);

        return "Transaction PIN created successfully";
    }

}
