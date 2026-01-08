package com.example.demo.controller;

import com.example.demo.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // DEPOSIT
    @PostMapping("/deposit")
    public String deposit(@RequestBody Map<String, String> req) {
        transactionService.deposit(
                req.get("accountNumber"),
                new BigDecimal(req.get("amount"))
        );
        return "Deposit successful";
    }

    // WITHDRAW
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody Map<String, String> req) {
        transactionService.withdraw(
                req.get("accountNumber"),
                new BigDecimal(req.get("amount"))
        );
        return "Withdraw successful";
    }

    // TRANSFER
    @PostMapping("/transfer")
    public String transfer(@RequestBody Map<String, String> req) {
        transactionService.transfer(
                req.get("fromAccount"),
                req.get("toAccount"),
                new BigDecimal(req.get("amount"))
        );
        return "Transfer successful";
    }
}
