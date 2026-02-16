package com.example.demo.controller;

import com.example.demo.dto.AccountResponseDTO;
import com.example.demo.entity.Account;
import com.example.demo.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin
public class AccountController {

    private final AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/me")
    public AccountResponseDTO getMyAccount(Authentication authentication) {

        String email = authentication.getName();
        LoggerFactory.getLogger(AccountController.class).info("Fetching account details for user: {}", email);

        Account account = accountService.getMyAccount(email);
        LoggerFactory.getLogger(AccountController.class).info("Account details retrieved for user: {}", email);

        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }


    @GetMapping("/{accountNumber}")
    public AccountResponseDTO getAccount(@PathVariable String accountNumber) {

        Account account = accountService.getAccount(accountNumber);
        LoggerFactory.getLogger(AccountController.class).info("Account details retrieved for account number: {}", accountNumber);

        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }
}
