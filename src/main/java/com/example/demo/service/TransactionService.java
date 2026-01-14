package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;


    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService, PasswordEncoder passwordEncoder) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
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
    public void withdraw(String accountNumber,
                         BigDecimal amount,
                         String pin) {

        validateAmount(amount);

        Account account = accountService.getAccount(accountNumber);
        validatePin(account, pin);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        accountService.updateBalance(
                account,
                account.getBalance().subtract(amount)
        );

        transactionRepository.save(
                Transaction.builder()
                        .type("WITHDRAW")
                        .amount(amount)
                        .transactionDate(LocalDateTime.now())
                        .account(account)
                        .build()
        );
    }


    // ================= TRANSFER =================
    public void transfer(String fromAccount,
                         String toAccount,
                         BigDecimal amount,
                         String pin) {

        validateAmount(amount);

        Account sender = accountService.getAccount(fromAccount);
        Account receiver = accountService.getAccount(toAccount);

        validatePin(sender, pin);

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        accountService.updateBalance(
                sender, sender.getBalance().subtract(amount)
        );

        accountService.updateBalance(
                receiver, receiver.getBalance().add(amount)
        );

        transactionRepository.save(
                Transaction.builder()
                        .type("TRANSFER_OUT")
                        .amount(amount)
                        .transactionDate(LocalDateTime.now())
                        .account(sender)
                        .build()
        );

        transactionRepository.save(
                Transaction.builder()
                        .type("TRANSFER_IN")
                        .amount(amount)
                        .transactionDate(LocalDateTime.now())
                        .account(receiver)
                        .build()
        );
    }



    // ================= VALIDATION =================
    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }

    private void validatePin(Account account, String rawPin){
        User user = account.getUser();
        if(user.getTransactionPin() == null){
            throw new BadRequestException("Transaction PIN not set");
        }

        if(!passwordEncoder.matches(rawPin, user.getTransactionPin())){
            throw new BadRequestException("Invalid transaction PIN");
        }
    }
}
