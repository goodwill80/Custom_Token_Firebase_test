package com.jonathan.firebase_custom_token.Service;

import com.jonathan.firebase_custom_token.DataPojo.SignupData;
import com.jonathan.firebase_custom_token.Entity.Role;
import com.jonathan.firebase_custom_token.Entity.Users;
import com.jonathan.firebase_custom_token.Repository.TokenRepository;
import com.jonathan.firebase_custom_token.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    TokenRepository tokenRepo;

    public void signup(SignupData obj) throws RuntimeException {
        Optional<Users> findUser = userRepo.findByUsername(obj.getUsername());
        if(findUser.isEmpty()) {
            Users user = new Users();
            user.setRole(Role.USER);
            user.setUsername(obj.getUsername());
            user.setUid(obj.getUid());
            userRepo.save(user);
        } else {
            throw new RuntimeException("User is already registered!");
        }
    }


}
