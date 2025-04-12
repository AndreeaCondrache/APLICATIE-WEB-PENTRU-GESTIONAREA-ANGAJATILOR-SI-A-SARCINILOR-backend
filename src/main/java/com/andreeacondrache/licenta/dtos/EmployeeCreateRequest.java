package com.andreeacondrache.licenta.dtos;
import com.andreeacondrache.licenta.enums.Department;
import lombok.Data;

@Data
public class EmployeeCreateRequest {
    private String name;
    private String email;
    private String password;
    private Department department;
}
