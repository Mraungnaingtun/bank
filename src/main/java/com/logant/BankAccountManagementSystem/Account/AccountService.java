package com.logant.BankAccountManagementSystem.Account;

import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.Transaction.TransactionRepository;
import com.logant.BankAccountManagementSystem.Enum.TransactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;



    // Deposit money into an account
    @Transactional
    public void deposit(Long accountId, Double amount) {
        Account account = findAccountById(accountId);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        recordTransaction(account, amount, TransactionType.DEPOSIT);
    }

    // Withdraw money from an account
    @Transactional
    public void withdraw(Long accountId, Double amount) {
        Account account = findAccountById(accountId);
        if (amount > account.getBalance()) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        recordTransaction(account, amount, TransactionType.WITHDRAWAL);
    }

    // Transfer money between accounts
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, Double amount) {
        Account fromAccount = findAccountById(fromAccountId);
        Account toAccount = findAccountById(toAccountId);

        if (amount > fromAccount.getBalance()) {
            throw new IllegalArgumentException("Insufficient balance for transfer.");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record the transactions for both accounts
        recordTransaction(fromAccount, amount, TransactionType.TRANSFER);
        recordTransaction(toAccount, amount, TransactionType.DEPOSIT); // Receiving account will show as deposit
    }

    // Check the balance of an account
    public Double checkBalance(Long accountId) {
        Account account = findAccountById(accountId);
        return account.getBalance();
    }

    // Helper method to find account by id
    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));
    }

    // Helper method to record a transaction
    private void recordTransaction(Account account, Double amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public
    Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAllAccountsWithUsers() {
        return accountRepository.findAllWithUsers();
    }

}