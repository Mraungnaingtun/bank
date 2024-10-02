package com.logant.BankAccountManagementSystem.Transaction;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.logant.BankAccountManagementSystem.Account.Account;
import com.logant.BankAccountManagementSystem.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonBackReference("account-transaction")
    private Account account;

}