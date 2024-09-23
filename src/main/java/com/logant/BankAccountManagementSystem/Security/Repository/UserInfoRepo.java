package com.logant.BankAccountManagementSystem.Security.Repository;


import com.logant.BankAccountManagementSystem.Auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserInfoRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmailId(String emailId);

    Optional<User> findByUserName(String username);
}
