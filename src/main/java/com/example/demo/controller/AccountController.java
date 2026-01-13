package com.example.demo.controller;

import com.example.demo.dto.AccountResponseDTO;
import com.example.demo.entity.Account;
import com.example.demo.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ================= CUSTOMER: MY ACCOUNT =================
    @GetMapping("/me")
    public AccountResponseDTO getMyAccount(Authentication authentication) {

        String email = authentication.getName(); // 🔐 JWT principal

        Account account = accountService.getMyAccount(email);

        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    // ================= ADMIN / INTERNAL =================
    @GetMapping("/{accountNumber}")
    public AccountResponseDTO getAccount(@PathVariable String accountNumber) {

        Account account = accountService.getAccount(accountNumber);

        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }
}
