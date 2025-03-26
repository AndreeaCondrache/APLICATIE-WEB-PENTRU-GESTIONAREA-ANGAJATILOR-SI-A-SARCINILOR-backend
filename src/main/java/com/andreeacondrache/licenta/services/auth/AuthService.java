package com.andreeacondrache.licenta.services.auth;

import com.andreeacondrache.licenta.dtos.SignupRequest;
import com.andreeacondrache.licenta.dtos.UserDTO;

public interface AuthService {
    UserDTO signup(SignupRequest signupRequest);
    Boolean hasUserWithEmail(String email);
}
