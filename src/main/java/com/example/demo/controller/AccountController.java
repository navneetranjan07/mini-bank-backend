package com.example.demo.controller;


import com.example.demo.dto.AccountResponseDTO;
import com.example.demo.entity.Account;
import com.example.demo.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // GET ACCOUNT BY ACCOUNT NUMBER
    @GetMapping("/{accountNumber}")
    public AccountResponseDTO getAccount(@PathVariable String accountNumber) {

        Account account = accountService.getAccount(accountNumber);

        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

}
