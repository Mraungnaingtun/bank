package com.logant.BankAccountManagementSystem.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping
    public Transaction createUser(@RequestBody Transaction user) {
        return transactionService.createTransaction(user);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public Transaction getUserById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping
    public List<Transaction> getAllAccounts() {
        return transactionService.getAllTransactions();
    }

}