package com.example.demo.controller;

import com.example.demo.dto.AdminStatsDTO;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/stats")
    public AdminStatsDTO getStats() {

        BigDecimal totalBalance =
                accountRepository.findAll().stream()
                        .map(Account::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        long last24hTxns =
                transactionRepository.countByTransactionDateAfter(
                        LocalDateTime.now().minusHours(24)
                );

        return new AdminStatsDTO(
                userRepository.count(),
                accountRepository.count(),
                totalBalance,
                last24hTxns
        );
    }
}
