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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AccountServiceWidthdrawTest {

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
    public  void testsSuccessfulWithdraw(){
        Double withdraw_amount = 200.00;
        System.out.println("Withdraw Amount = " + withdraw_amount);

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));
        Double last_balance = myAccount.getBalance();
        System.out.println("Account Balance Before Withdraw = "+ last_balance);

        // Act - withdraw
        accountService.withdraw(myAccount.getId(),withdraw_amount);
        System.out.println("Account Balance after deposit = "+ myAccount.getBalance());
        // Assert
        assertEquals(last_balance - withdraw_amount, myAccount.getBalance());

        verify(accountRepository, times(1)).save(myAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }



    @Test
    public void testWithdrawZeroAmount() {
        Double withdrawAmount = 0.0;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));
        Double initialBalance = myAccount.getBalance();

        // Act
        accountService.withdraw(myAccount.getId(), withdrawAmount);

        // Assert
        assertEquals(initialBalance, myAccount.getBalance()); // No change in balance
        verify(accountRepository, times(1)).save(myAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


    @Test
    public void testTransactionDetails() {
        Double withdrawAmount = 200.00;

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));

        // Act
        accountService.withdraw(myAccount.getId(), withdrawAmount);

        // Assert
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(TransactionType.WITHDRAWAL, savedTransaction.getType());
        assertEquals(withdrawAmount, savedTransaction.getAmount(), 0.01);
        assertEquals(myAccount, savedTransaction.getAccount());
    }


    @Test
    public void testWithdrawInsufficientBalance() {
        Double withdrawAmount = 1200.00; // Greater than balance

        // Arrange
        when(accountRepository.findById(myAccount.getId())).thenReturn(Optional.of(myAccount));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(myAccount.getId(), withdrawAmount);
        });

        assertEquals("Insufficient balance.", exception.getMessage());
        verify(accountRepository, never()).save(myAccount); // Verify account isn't saved
        verify(transactionRepository, never()).save(any(Transaction.class)); // Verify no transaction
    }
}
