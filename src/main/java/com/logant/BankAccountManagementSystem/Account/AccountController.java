package com.logant.BankAccountManagementSystem.Account;

import com.logant.BankAccountManagementSystem.Account.DTO.TransferRequestDTO;
import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.Transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public Account createAccount(@RequestBody Account user) {
        return accountService.createAccount(user);
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Deposit money into an account using Map
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody Map<String, Object> request) {
        Long accountId = ((Number) request.get("accountId")).longValue();
        Double amount = (Double) request.get("amount");
        accountService.deposit(accountId, amount);
        return ResponseEntity.ok("Deposit successful");
    }

    // Withdraw money from an account using Map
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody Map<String, Object> request) {
        Long accountId = ((Number) request.get("accountId")).longValue();
        Double amount = (Double) request.get("amount");
        accountService.withdraw(accountId, amount);
        return ResponseEntity.ok("Withdrawal successful");
    }

    //using request by dto
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        accountService.transfer(transferRequestDTO.getFromAccountId(),
                transferRequestDTO.getToAccountId(),
                transferRequestDTO.getAmount());
        return ResponseEntity.ok("Transfer successful");
    }


    // Check the balance of an account
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Double> checkBalance(@PathVariable Long accountId) {
        Double balance = accountService.checkBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    // Get transaction history for an account
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/accounts-with-users")
    public List<Account> getAccountsWithUsers() {
        return accountService.getAllAccountsWithUsers();
    }

}