package com.logant.BankAccountManagementSystem.Account;

import com.logant.BankAccountManagementSystem.Account.DTO.TransferRequestDTO;
import com.logant.BankAccountManagementSystem.General.MainResponse;
import com.logant.BankAccountManagementSystem.General.ResponseCode;
import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.Transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping
    public Account createAccount(@RequestBody Account user) {
        return accountService.createAccount(user);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Deposit money into an account using Map
    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping("/deposit")
    public ResponseEntity<MainResponse> deposit(@RequestBody Map<String, Object> request) {
        Transaction ret = new Transaction();
        try {
            Long accountId = ((Number) request.get("accountId")).longValue();
            Double amount = (Double) request.get("amount");
            ret = accountService.deposit(accountId, amount);
        } catch (Exception e) {
            return MainResponse.buildErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return MainResponse.buildSuccessResponse(ResponseCode.SUCCESS, ret, HttpStatus.OK);
    }

    // Withdraw money from an account using Map
    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping("/withdraw")
    public ResponseEntity<MainResponse> withdraw(@RequestBody Map<String, Object> request) {

        Transaction ret = new Transaction();
        try {
            Long accountId = ((Number) request.get("accountId")).longValue();
            Double amount = (Double) request.get("amount");
            ret = accountService.withdraw(accountId, amount);
        } catch (Exception e) {
            return MainResponse.buildErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return MainResponse.buildSuccessResponse(ResponseCode.SUCCESS, ret, HttpStatus.OK);
    }

    // using request by dto
    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        accountService.transfer(transferRequestDTO.getFromAccountId(),
                transferRequestDTO.getToAccountId(),
                transferRequestDTO.getAmount());
        return ResponseEntity.ok("Transfer successful");
    }

    // Check the balance of an account
    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Double> checkBalance(@PathVariable Long accountId) {
        Double balance = accountService.checkBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    // Get transaction history for an account
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }
}