package com.example.demo.controller;

import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/transactions")
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(t -> TransactionResponseDTO.builder()
                        .type(t.getType())
                        .amount(t.getAmount())
                        .transactionDate(t.getTransactionDate())
                        .build())
                .toList();
    }

}