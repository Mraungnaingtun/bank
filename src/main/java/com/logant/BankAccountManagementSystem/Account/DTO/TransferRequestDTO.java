package com.logant.BankAccountManagementSystem.Account.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransferRequestDTO {
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
}
