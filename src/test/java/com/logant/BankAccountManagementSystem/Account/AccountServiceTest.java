package com.logant.BankAccountManagementSystem.Account;

import com.logant.BankAccountManagementSystem.Enum.AccountType;
import com.logant.BankAccountManagementSystem.Transaction.Transaction;
import com.logant.BankAccountManagementSystem.Transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

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
    public  void testsSuccessfulDeposit(){
        Double deposit_amount = 200.00;
        System.out.println("Deposit Amount = "+deposit_amount);

        // Arrange
        when(accountRepository.findById(fromAccount.getId())).thenReturn(Optional.of(fromAccount));
        Double last_balance = fromAccount.getBalance();
        System.out.println("Last Balance = "+ last_balance);

        // Act
        accountService.deposit(fromAccount.getId(),deposit_amount);
        System.out.println("Balance after deposit = "+ fromAccount.getBalance());
        // Assert
        assertEquals(last_balance + deposit_amount, fromAccount.getBalance());

        verify(accountRepository, times(1)).save(fromAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
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




}