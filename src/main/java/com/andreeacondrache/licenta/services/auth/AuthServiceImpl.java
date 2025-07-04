package com.andreeacondrache.licenta.services.auth;

import com.andreeacondrache.licenta.dtos.SignupRequest;
import com.andreeacondrache.licenta.dtos.UserDTO;
import com.andreeacondrache.licenta.entities.User;
import com.andreeacondrache.licenta.enums.UserRole;
import com.andreeacondrache.licenta.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import  java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements  AuthService{

    private final UserRepository userRepository;
    @PostConstruct
    private void createAnAdminAccount(){
        Optional<User> optionalUser = userRepository.findFirstByUserRole(UserRole.ADMIN);
    if(optionalUser.isEmpty()){
        User newAdmin = new User();
        newAdmin.setName("Admin");
        newAdmin.setEmail("admin@test.com");
        newAdmin.setPassword(new BCryptPasswordEncoder().encode("admin"));
        newAdmin.setUserRole(UserRole.ADMIN);
        userRepository.save(newAdmin);
    } else{
        System.out.println("Admin account already exist!");
    }
}

    @Override
    public UserDTO signup(SignupRequest signupRequest) {
       User user =new User();
       user.setEmail(signupRequest.getEmail());
       user.setName(signupRequest.getName());
       user.setUserRole(UserRole.EMPLOYEE);
       user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
      return  userRepository.save(user).getUserDTO();

    }

    @Override
    public Boolean hasUserWithEmail(String email) {

        return userRepository.findFirstByEmail(email).isPresent();
    }
}