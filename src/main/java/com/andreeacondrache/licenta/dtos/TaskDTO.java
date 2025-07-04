package com.andreeacondrache.licenta.dtos;

import com.andreeacondrache.licenta.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

import com.andreeacondrache.licenta.entities.Task;

@Data
public class TaskDTO {

    private Long id;

    private String title;

    private Date dueDate;

    private String description;

    private String priority;

    private TaskStatus taskStatus;

    private  Long employeeId;

    private String employeeName;



}
