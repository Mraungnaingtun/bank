package com.logant.BankAccountManagementSystem.Auth.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
    public class LoginRequestDto {
        private String username;
        private String password;

        // Getters and setters
    }

