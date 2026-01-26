package com.example.demo.controller;

import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.dto.UserTransactionDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

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



    @GetMapping("/transactions/user-wise")
    public List<UserTransactionDTO> getUserWiseTransactions() {

        return transactionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(t -> t.getAccount().getUser()))
                .entrySet()
                .stream()
                .map(entry -> {
                    var user = entry.getKey();
                    var txns = entry.getValue();

                    return UserTransactionDTO.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .totalTransactions(txns.size())
                            .transactions(
                                    txns.stream()
                                            .map(t -> TransactionResponseDTO.builder()
                                                    .type(t.getType())
                                                    .amount(t.getAmount())
                                                    .transactionDate(t.getTransactionDate())
                                                    .build())
                                            .toList()
                            )
                            .build();
                })
                .toList();
    }

    public String unlockUser(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLocked(false);
        user.setFailedPinAttempts(0);
        user.setFailedPinAttempts(0);
        userRepository.save(user);
        return "User account unlocked successfully";
    }



}