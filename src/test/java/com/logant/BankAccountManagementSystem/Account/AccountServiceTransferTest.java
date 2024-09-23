package com.logant.BankAccountManagementSystem.Account;

import com.logant.BankAccountManagementSystem.Enum.AccountType;
import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.Transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTransferTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountService accountService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    public void setup() {
        accountRepository = Mockito.mock(AccountRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        accountService = new AccountService(accountRepository, transactionRepository);

        fromAccount = Account.builder()
                .id(1L)
                .accountNumber("123456789")
                .balance(1000.0)
                .accountType(AccountType.SAVINGS)
                .customer(null)
                .build();
        toAccount = Account.builder()
                .id(2L)
                .accountNumber("123456789")
                .balance(2000.0)
                .accountType(AccountType.SAVINGS)
                .customer(null)
                .build();
    }


    @Test
    public void testTransferInsufficientBalance() {
        // Arrange
        Long fromAccountId = fromAccount.getId();
        Long toAccountId = toAccount.getId();
        Double amount = 1200.0; // More than available balance

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.transfer(fromAccountId, toAccountId, amount);
        });

        verify(accountRepository, never()).save(fromAccount);
        verify(accountRepository, never()).save(toAccount);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testTransferToSelf() {
        // Arrange
        Long fromAccountId = fromAccount.getId();
        Long toAccountId = fromAccount.getId(); // Transferring to self
        Double amount = 200.0;

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));

        // Act
        accountService.transfer(fromAccountId, toAccountId, amount);

        // Assert
        assertEquals(1000.0, fromAccount.getBalance()); // Balance should remain unchanged
        verify(accountRepository, times(1)).save(fromAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class)); // Only one transaction
    }


    @Test
    public void testTransferZeroOrNegativeAmount() {
        // Arrange
        Long fromAccountId = fromAccount.getId();
        Long toAccountId = toAccount.getId();

        // Act & Assert for zero amount
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.transfer(fromAccountId, toAccountId, 0.0);
        });

        // Act & Assert for negative amount
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.transfer(fromAccountId, toAccountId, -100.0);
        });
    }

    @Test
    public void testTransferAccountNotFound() {
        // Arrange
        Long fromAccountId = fromAccount.getId();
        Long toAccountId = 999L; // Non-existing account

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transfer(fromAccountId, toAccountId, 200.0);
        });

        assertEquals("Account not found.", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testTransferSuccessful() {

        // Arrange
        Long fromAccountId = fromAccount.getId();
        Long toAccountId = toAccount.getId();
        Double amount = 300.0;

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        // Act
        accountService.transfer(fromAccountId, toAccountId, amount);

        // Assert
        assertEquals(700.0, fromAccount.getBalance());
        assertEquals(2300.0, toAccount.getBalance());

        verify(accountRepository, times(1)).save(fromAccount);
        verify(accountRepository, times(1)).save(toAccount);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }


    @Test
    public void testMultipleTransfers() {
        // Arrange
        Long fromAccountId = fromAccount.getId();
        Long toAccountId = toAccount.getId();
        Double amount1 = 200.0;
        Double amount2 = 100.0;

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        // Act - first transfer
        accountService.transfer(fromAccountId, toAccountId, amount1);
        // Act - second transfer
        accountService.transfer(fromAccountId, toAccountId, amount2);

        // Assert
        assertEquals(700.0 - amount2, fromAccount.getBalance());
        assertEquals(2300.0 + amount1 + amount2, toAccount.getBalance());

        verify(accountRepository, times(2)).save(fromAccount);
        verify(accountRepository, times(2)).save(toAccount);
        verify(transactionRepository, times(4)).save(any(Transaction.class));
    }




}