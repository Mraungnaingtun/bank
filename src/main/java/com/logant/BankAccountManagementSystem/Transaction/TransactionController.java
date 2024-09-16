package com.logant.BankAccountManagementSystem.Transaction;

import com.logant.BankAccountManagementSystem.Account.Account;
import com.logant.BankAccountManagementSystem.Account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public Transaction createUser(@RequestBody Transaction user) {
        return transactionService.createTransaction(user);
    }

    @GetMapping("/{id}")
    public Transaction getUserById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping
    public List<Transaction> getAllAccounts() {
        return transactionService.getAllTransactions();
    }

}