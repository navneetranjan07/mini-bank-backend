package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(noRollbackFor = BadRequestException.class)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository,
                              AuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    /* ======================= DEPOSIT ======================= */

    public void deposit(String accountNumber, BigDecimal amount) {

        validateAmount(amount);

        Account account = accountService.getAccount(accountNumber);
        User user = account.getUser();

        if (user.isLocked()) {
            throw new BadRequestException(
                    "User locked, please visit your branch"
            );
        }

        BigDecimal updatedBalance = account.getBalance().add(amount);
        accountService.updateBalance(account, updatedBalance);

        transactionRepository.save(
                Transaction.builder()
                        .type("DEPOSIT")
                        .amount(amount)
                        .transactionDate(LocalDateTime.now())
                        .account(account)
                        .build()
        );
    }

    /* ======================= WITHDRAW ======================= */

    public void withdraw(String accountNumber,
                         BigDecimal amount,
                         String pin) {

        validateAmount(amount);

        Account account = accountService.getAccount(accountNumber);
        validatePinWithLock(account, pin);

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

    /* ======================= TRANSFER ======================= */

    public void transfer(String fromAccount,
                         String toAccount,
                         BigDecimal amount,
                         String pin) {

        validateAmount(amount);

        if (fromAccount == null || toAccount == null) {
            throw new BadRequestException("Account numbers cannot be null");
        }

        Account sender = accountService.getAccount(fromAccount);
        Account receiver = accountService.getAccount(toAccount);

        validatePinWithLock(sender, pin);
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        accountService.updateBalance(
                sender,
                sender.getBalance().subtract(amount)
        );

        accountService.updateBalance(
                receiver,
                receiver.getBalance().add(amount)
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

    /* ======================= HELPERS ======================= */

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }

    /**
     * 🔐 REAL BANK LOGIC:
     * - If user is locked → block immediately
     * - Track failed PIN attempts
     * - Lock user on 3rd failure
     */
    private void validatePinWithLock(Account account, String rawPin) {

        User user = account.getUser();

        if (user.isLocked()) {
            throw new BadRequestException(
                    "User locked, please visit your branch"
            );
        }

        if (user.getTransactionPin() == null) {
            throw new BadRequestException("Transaction PIN not set");
        }

        if (!passwordEncoder.matches(rawPin, user.getTransactionPin())) {

            int attempts = user.getFailedPinAttempts() + 1;
            user.setFailedPinAttempts(attempts);

            if (attempts >= 3) {
                user.setLocked(true);
                user.setFailedPinAttempts(0);
                userRepository.save(user);

                throw new BadRequestException(
                        "User locked, please visit your branch"
                );
            }

            userRepository.save(user);

            throw new BadRequestException(
                    "Invalid transaction PIN. Attempts left: " + (3 - attempts)
            );
        }

       // Reset failed attempts on successful PIN entry
        user.setFailedPinAttempts(0);
        userRepository.save(user);
    }
}
