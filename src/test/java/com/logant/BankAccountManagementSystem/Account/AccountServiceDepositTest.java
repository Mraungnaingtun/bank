package com.logant.BankAccountManagementSystem.Account;

import com.logant.BankAccountManagementSystem.Enum.AccountType;
import com.logant.BankAccountManagementSystem.Enum.TransactionType;
import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.Transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceDepositTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountService accountService;

    private Account myAccount;

    @BeforeEach
    public void setup() {
        accountRepository = Mockito.mock(AccountRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        accountService = new AccountService(accountRepository, transactionRepository);

        myAccount = Account.builder()
                .id(1L)
                .accountNumber("123456789")
                .balance(1000.0)
                .accountType(AccountType.SAVINGS)
                .customer(null)
                .build();
    }

    @Test
    public void testSuccessfulDeposit() {
        Double depositAmount = 200.00;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));
        Double initialBalance = myAccount.getBalance();

        // Act - deposit
        accountService.deposit(myAccount.getId(), depositAmount);

        // Assert - verify balance update
        assertEquals(initialBalance + depositAmount, myAccount.getBalance(), 0.01);
        verify(accountRepository, times(1)).save(myAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testDepositZeroAmount() {
        Double depositAmount = 0.0;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));

        // Act & Assert - ensure exception for zero amount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.deposit(myAccount.getId(), depositAmount);
        });

        assertEquals("Amount must be greater than zero.", exception.getMessage());
        verify(accountRepository, never()).save(myAccount); // Ensure no save call
        verify(transactionRepository, never()).save(any(Transaction.class)); // No transaction recorded
    }

    @Test
    public void testDepositNegativeAmount() {
        Double depositAmount = -50.0;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));

        // Act & Assert - ensure exception for negative amount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.deposit(myAccount.getId(), depositAmount);
        });

        assertEquals("Amount must be greater than zero.", exception.getMessage());
        verify(accountRepository, never()).save(myAccount); // Ensure no save call
        verify(transactionRepository, never()).save(any(Transaction.class)); // No transaction recorded
    }

    @Test
    public void testTransactionDetailsForDeposit() {
        Double depositAmount = 500.00;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));

        // Act
        accountService.deposit(myAccount.getId(), depositAmount);

        // Assert - capture the transaction details
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
        assertEquals(depositAmount, savedTransaction.getAmount(), 0.01);
        assertEquals(myAccount, savedTransaction.getAccount());
    }

    @Test
    public void testLargeDeposit() {
        Double depositAmount = 1000000.0;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));
        Double initialBalance = myAccount.getBalance();

        // Act - large deposit
        accountService.deposit(myAccount.getId(), depositAmount);

        // Assert - verify large balance update
        assertEquals(initialBalance + depositAmount, myAccount.getBalance(), 0.01);
        verify(accountRepository, times(1)).save(myAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
