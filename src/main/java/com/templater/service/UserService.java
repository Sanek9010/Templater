package com.templater.service;

import com.templater.domain.User;
import com.templater.repositories.UserRepository;
import com.templater.security.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveAsUser(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Authority autority = new Authority();
        autority.setAuthority("ROLE_USER");
        autority.setUser(user);
        user.getAuthorities().add(autority);
        return userRepo.save(user);
    }
}
