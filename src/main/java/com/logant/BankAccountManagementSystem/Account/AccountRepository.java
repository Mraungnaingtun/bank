package com.logant.BankAccountManagementSystem.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.user")
    List<Account> findAllWithUsers();
}