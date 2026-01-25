package com.example.demo.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionDTO {
    private Long userId;
    private String name;
    private String email;
    private int totalTransactions;
    private List<TransactionResponseDTO> transactions;
}

