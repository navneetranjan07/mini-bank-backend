package com.example.demo.controller;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody Map<String, String> req) {

        User user = userService.register(
                req.get("name"),
                req.get("email"),
                req.get("password")
        );

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {

        User user = userService.login(
                req.get("email"),
                req.get("password")
        );

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return Map.of(
                "token", token,
                "role", user.getRole().name()
        );
    }
}
