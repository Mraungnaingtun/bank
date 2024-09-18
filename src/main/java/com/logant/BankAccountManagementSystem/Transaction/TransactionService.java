package com.logant.BankAccountManagementSystem.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(Transaction account) {
        return transactionRepository.save(account);
    }

    public
    Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get all transactions for a specific account
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}