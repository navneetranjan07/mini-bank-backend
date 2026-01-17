package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePinWithPasswordRequest {
    private String password;
    private String pin;
}
