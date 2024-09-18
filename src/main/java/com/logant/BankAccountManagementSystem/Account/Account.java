package com.logant.BankAccountManagementSystem.Account;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.User.User;
import com.logant.BankAccountManagementSystem.Enum.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private Double balance;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Transaction> transactions;
}