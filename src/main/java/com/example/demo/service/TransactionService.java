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

        Account sender = accountService.getAccount(fromAccount);
        Account receiver = accountService.getAccount(toAccount);

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        // 1️⃣ Update balances
        accountService.updateBalance(
                sender,
                sender.getBalance().subtract(amount)
        );

        accountService.updateBalance(
                receiver,
                receiver.getBalance().add(amount)
        );

        // 2️⃣ Sender transaction (TRANSFER OUT)
        Transaction debitTxn = Transaction.builder()
                .type("TRANSFER_OUT")
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .account(sender)
                .build();

        // 3️⃣ Receiver transaction (TRANSFER IN / CREDIT)
        Transaction creditTxn = Transaction.builder()
                .type("TRANSFER_IN")
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .account(receiver)
                .build();

        transactionRepository.save(debitTxn);
        transactionRepository.save(creditTxn);
    }


    // ================= VALIDATION =================
    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }
}
