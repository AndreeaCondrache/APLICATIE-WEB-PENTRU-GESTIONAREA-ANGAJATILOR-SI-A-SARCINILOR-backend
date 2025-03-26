package com.andreeacondrache.licenta.dtos;

import com.andreeacondrache.licenta.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;

    private Long userId;

    private UserRole userRole;
}
