package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionPinRequest {
    private String accountNumber;
    private String pin;
    private String toAccount;
    private BigDecimal amount;
}
