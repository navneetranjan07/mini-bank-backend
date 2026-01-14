package com.example.demo.controller;

import com.example.demo.dto.TransactionPinRequest;
import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.entity.Account;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionController(
            TransactionService transactionService,
            TransactionRepository transactionRepository,
            AccountService accountService
    ) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    // ================= DEPOSIT =================
    @PostMapping("/deposit")
    public String deposit(@RequestBody Map<String, String> req) {
        transactionService.deposit(
                req.get("accountNumber"),
                new BigDecimal(req.get("amount"))
        );
        return "Deposit successful";
    }

    // ================= WITHDRAW =================
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionPinRequest req) {
        transactionService.withdraw(
                req.getAccountNumber(),
                new BigDecimal(req.getAmount()),
                req.getPin()
        );
        return "Withdraw successful";
    }


    // ================= TRANSFER =================
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionPinRequest req) {
        transactionService.transfer(
                req.getAccountNumber(),
                req.getToAccount(),
                new BigDecimal(req.getAmount()),
                req.getPin()
        );
        return "Transfer successful";
    }


    // ================= USER TRANSACTION HISTORY =================
    @GetMapping("/my")
    public List<TransactionResponseDTO> myTransactions(Authentication auth) {

        String email = auth.getName();
        Account account = accountService.getMyAccount(email);

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
