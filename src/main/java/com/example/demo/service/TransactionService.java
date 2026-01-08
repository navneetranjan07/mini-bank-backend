package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    // ================= DEPOSIT =================
    public void deposit(String accountNumber, BigDecimal amount) {

        validateAmount(amount);

        Account account = accountService.getAccount(accountNumber);

        BigDecimal updatedBalance = account.getBalance().add(amount);
        accountService.updateBalance(account, updatedBalance);

        Transaction txn = Transaction.builder()
                .type("DEPOSIT")
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(txn);
    }

    // ================= WITHDRAW =================
    public void withdraw(String accountNumber, BigDecimal amount) {

        validateAmount(amount);

        Account account = accountService.getAccount(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        BigDecimal updatedBalance = account.getBalance().subtract(amount);
        accountService.updateBalance(account, updatedBalance);

        Transaction txn = Transaction.builder()
                .type("WITHDRAW")
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(txn);
    }

    // ================= TRANSFER =================
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {

        validateAmount(amount);

        withdraw(fromAccount, amount);
        deposit(toAccount, amount);
    }

    // ================= VALIDATION =================
    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }
}
