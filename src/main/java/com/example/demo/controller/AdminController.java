package com.example.demo.controller;


import com.example.demo.dto.AccountResponseDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminController(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // GET ALL USERS
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserResponseDTO.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .locked(u.isLocked())
                        .build())
                .toList();
    }


    // GET ALL ACCOUNTS
    @GetMapping("/accounts")
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(a -> AccountResponseDTO.builder()
                        .accountNumber(a.getAccountNumber())
                        .balance(a.getBalance())
                        .build())
                .toList();
    }


    // DELETE USER
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    @PutMapping("/user/{id}/lock")
    public String lockUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLocked(true);
        userRepository.save(user);
        return "User locked successfully";
    }

    @PutMapping("/user/{id}/unlock")
    public String unlockUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLocked(false);
        userRepository.save(user);
        return "User unlocked successfully";
    }
}