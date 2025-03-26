package com.andreeacondrache.licenta.dtos;
import com.andreeacondrache.licenta.enums.UserRole;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDTO  {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
}
