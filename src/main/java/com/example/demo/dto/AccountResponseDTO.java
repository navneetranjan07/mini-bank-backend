package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AccountResponseDTO {
    private String accountNumber;
    private BigDecimal balance;
}
