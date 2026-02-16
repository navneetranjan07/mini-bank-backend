package com.example.demo.controller;

import com.example.demo.dto.AdminStatsDTO;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdminStatsController.class);

    @GetMapping("/stats")
    public AdminStatsDTO getStats() {
        LoggerFactory.getLogger(AdminStatsController.class).info("Fetching admin dashboard stats");

        BigDecimal totalBalance =
                accountRepository.findAll().stream()
                        .map(Account::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        LoggerFactory.getLogger(AdminStatsController.class).info("Total balance across all accounts calculated: {}", totalBalance);

        long last24hTxns =
                transactionRepository.countByTransactionDateAfter(
                        LocalDateTime.now().minusHours(24)
                );
        LoggerFactory.getLogger(AdminStatsController.class).info("Transactions in the last 24 hours counted: {}", last24hTxns);

        return new AdminStatsDTO(
                userRepository.count(),
                accountRepository.count(),
                totalBalance,
                last24hTxns
        );
    }

    @GetMapping("/transactions/daily")
    public Map<LocalDate, Long> dailyTransactions() {
        LoggerFactory.getLogger(AdminStatsController.class).info("Fetching daily transaction counts for admin dashboard");
        return transactionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().toLocalDate(),
                        Collectors.counting()
                ));
    }

}
