package com.example.demo.controller;


import com.example.demo.dto.AccountResponseDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    // GET ALL USERS
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        LoggerFactory.getLogger(AdminController.class).info("Fetching all users for admin dashboard");
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
        LoggerFactory.getLogger(AdminController.class).info("Fetching all accounts for admin dashboard");
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
        LoggerFactory.getLogger(AdminController.class).info("Deleted user with id: {}", id);
        return "User deleted successfully";
    }

    @PutMapping("/user/{id}/lock")
    public String lockUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLocked(true);
        userRepository.save(user);
        LoggerFactory.getLogger(AdminController.class).info("Locked user with id: {}", id);
        return "User locked successfully";
    }

    @PutMapping("/user/{id}/unlock")
    public String unlockUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLocked(false);
        userRepository.save(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();
        auditService.log(
                adminEmail,
                "UNLOCK_USER",
                user.getEmail(),
                "SUCCESS",
                null,
                "Unlocked by admin"
        );
        LoggerFactory.getLogger(AdminController.class).info("Unlocked user with id: {}", id);

        return "User unlocked successfully";
    }

}