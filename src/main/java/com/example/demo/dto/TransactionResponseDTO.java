package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionResponseDTO {

    private String type;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
}
