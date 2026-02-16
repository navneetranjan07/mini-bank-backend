package com.example.demo.controller;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody Map<String, String> req) {

        User user = userService.register(
                req.get("name"),
                req.get("email"),
                req.get("password")
        );
LoggerFactory .getLogger(AuthController.class).info("New user registered with email: {}", user.getEmail());
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }


    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {

        User user = userService.login(
                req.get("email"),
                req.get("password")
        );
        LoggerFactory.getLogger(AuthController.class).info("User logged in with email: {}", user.getEmail());

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
        LoggerFactory.getLogger(AuthController.class).info("JWT token generated for user: {}", user.getEmail());

        return Map.of(
                "token", token,
                "role", user.getRole().name()
        );
    }
}
