package com.andreeacondrache.licenta.services.jwt;

import com.andreeacondrache.licenta.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

UserDetailsService userDetailsService();
}
