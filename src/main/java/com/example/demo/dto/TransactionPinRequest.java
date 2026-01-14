package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionPinRequest {
    private String accountNumber;
    private String pin;
    private String toAccount;
    private String amount;
}
