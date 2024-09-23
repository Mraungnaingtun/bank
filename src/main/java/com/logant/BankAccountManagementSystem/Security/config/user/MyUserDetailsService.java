package com.logant.BankAccountManagementSystem.Security.config.user;



import com.logant.BankAccountManagementSystem.Security.Repository.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserInfoRepo userInfoRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Trying to load user by email: { "+username +"}");
        UserDetails res = userInfoRepo
                .findByEmailId(username)
                .map(MyUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("UserEmail: "+ username +" does not exist"));
        return res;
    }
}
