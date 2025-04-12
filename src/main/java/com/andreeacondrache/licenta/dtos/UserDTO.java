package com.andreeacondrache.licenta.dtos;
import com.andreeacondrache.licenta.enums.Department;
import com.andreeacondrache.licenta.enums.UserRole;

import lombok.Data;
import lombok.Getter;

@Data
public class UserDTO  {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;

    @Getter
    private Department department;

    public void setDepartment(Department department) {
        this.department = department;
    }


}
