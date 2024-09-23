package com.logant.BankAccountManagementSystem.Security.Service;


import com.logant.BankAccountManagementSystem.Auth.User;
import com.logant.BankAccountManagementSystem.Security.Repository.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserInfoRepo userInfoRepo;

    public Optional<User> findByName(String name){
        return userInfoRepo.findByUserName(name);
    }

    public Optional<User> findByEmail(String email){
        return userInfoRepo.findByEmailId(email);
    }
}
