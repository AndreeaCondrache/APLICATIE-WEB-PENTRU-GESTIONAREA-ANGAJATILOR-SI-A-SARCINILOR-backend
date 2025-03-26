package com.andreeacondrache.licenta.controllers.employee;

import com.andreeacondrache.licenta.dtos.CommentDTO;
import com.andreeacondrache.licenta.dtos.TaskDTO;
import com.andreeacondrache.licenta.services.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTaskByUserId(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.getTasksByUserId(id));

    }
    @GetMapping("/task/{id}/{status}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @PathVariable String status){
        TaskDTO updatedTaskDTO = employeeService.updateTask(id, status);
        if(updatedTaskDTO == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDTO);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getTaskById(id));
    }


    @PostMapping("/task/comment")
    public ResponseEntity<?> createComment(@RequestParam Long taskId, @RequestParam Long postedBy, @RequestBody String content){
        CommentDTO createdCommentDTO = employeeService.createComment(taskId, postedBy,content);

        if (createdCommentDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommentDTO);
    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(employeeService.getCommentsByTask(taskId));

    }
}