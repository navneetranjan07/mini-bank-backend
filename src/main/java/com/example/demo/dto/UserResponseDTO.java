package com.example.demo.dto;

import com.example.demo.entity.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean locked;
}
