package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ================= CUSTOMER: MY ACCOUNT =================
    public Account getMyAccount(String email) {

        return accountRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
    }

    // ================= ADMIN / INTERNAL =================
    public Account getAccount(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
    }

    // ================= UPDATE BALANCE =================
    public void updateBalance(Account account, BigDecimal newBalance) {

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Balance cannot be negative");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }
}
