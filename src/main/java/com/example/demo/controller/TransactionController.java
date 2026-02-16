package com.example.demo.controller;

import com.example.demo.dto.TransactionPinRequest;
import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.entity.Account;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private static final Logger  logger = Logger.getLogger(TransactionController.class.getName());

    public TransactionController(
            TransactionService transactionService,
            TransactionRepository transactionRepository,
            AccountService accountService
    ) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }


    @PostMapping("/deposit")
    public String deposit(@RequestBody Map<String, String> req) {
        transactionService.deposit(
                req.get("accountNumber"),
                new BigDecimal(req.get("amount"))
        );
        Logger.getLogger(TransactionController.class.getName()).info("Deposit of amount " + req.get("amount") + " to account " + req.get("accountNumber") + " successful");
        return "Deposit successful";
    }


    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionPinRequest req) {

        if (req.getAccountNumber() == null) {
            throw new BadRequestException("Account number is required");
        }

        transactionService.withdraw(
                req.getAccountNumber(),
                req.getAmount(),
                req.getPin()
        );

        Logger.getLogger(TransactionController.class.getName()).info("Withdrawal of amount " + req.getAmount() + " from account " + req.getAccountNumber() + " successful");

        return "Withdraw successful";
    }



    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionPinRequest req) {

        if (req.getAccountNumber() == null || req.getToAccount() == null) {
            throw new BadRequestException("Both account numbers are required");
        }

        transactionService.transfer(
                req.getAccountNumber(),
                req.getToAccount(),
                req.getAmount(),
                req.getPin()
        );
Logger.getLogger(TransactionController.class.getName()).info("Transfer of amount " + req.getAmount() + " from account " + req.getAccountNumber() + " to account " + req.getToAccount() + " successful");
        return "Transfer successful";
    }



    @GetMapping("/my")
    public List<TransactionResponseDTO> myTransactions(Authentication auth) {

        String email = auth.getName();
        Account account = accountService.getMyAccount(email);
        Logger.getLogger(TransactionController.class.getName()).info("Fetching transactions for user: " + email);

        return transactionRepository.findByAccountId(account.getId())
                .stream()
                .map(txn -> TransactionResponseDTO.builder()
                        .type(txn.getType())
                        .amount(txn.getAmount())
                        .transactionDate(txn.getTransactionDate())
                        .build()
                )
                .toList();
    }
}
