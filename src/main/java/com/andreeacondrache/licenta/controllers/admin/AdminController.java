package com.andreeacondrache.licenta.controllers.admin;

import com.andreeacondrache.licenta.dtos.CommentDTO;
import com.andreeacondrache.licenta.dtos.EmployeeCreateRequest;
import com.andreeacondrache.licenta.dtos.TaskDTO;
import com.andreeacondrache.licenta.dtos.UserDTO;
import com.andreeacondrache.licenta.entities.User;
import com.andreeacondrache.licenta.enums.Department;
import com.andreeacondrache.licenta.enums.UserRole;
import com.andreeacondrache.licenta.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.andreeacondrache.licenta.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository; // ✅ injectează repo
    private final PasswordEncoder passwordEncoder; // ✅ injectează encoder

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(adminService.getUsers());

    }

    @PostMapping("/task")
    public ResponseEntity<?> postTask(@RequestBody TaskDTO taskDTO){
        TaskDTO createdTaskDTO = adminService.postTask(taskDTO);

        if (createdTaskDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getTasks(){
        return ResponseEntity.ok(adminService.getTasks());
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id){
        return ResponseEntity.ok(adminService.getTaskById(id));
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id){
        adminService.deleteTask(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/tasks/search/{title}")
    public ResponseEntity<?> searchTask(@PathVariable String title){
        return ResponseEntity.ok(adminService.searchTask(title));
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO){
        TaskDTO updatedTaskDTO = adminService.updateTask(id, taskDTO);

        if (updatedTaskDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDTO);
    }

    @PostMapping("/task/comment")
    public ResponseEntity<?> createComment(@RequestParam Long taskId, @RequestParam Long postedBy, @RequestBody String content){
     CommentDTO createdCommentDTO = adminService.createComment(taskId, postedBy,content);

        if (createdCommentDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommentDTO);
    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentByTask(@PathVariable Long taskId){
        return ResponseEntity.ok(adminService.getCommentsByTask(taskId));

    }

    @PostMapping("/employee")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeCreateRequest request) {
        if (userRepository.findFirstByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).
                    body(Map.of("message", "Email deja folosit" ));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 🔐
        user.setUserRole(UserRole.EMPLOYEE);
        user.setDepartment(request.getDepartment());

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Angajat adăugat cu succes!"));

    }


    // în AdminController.java
    @GetMapping("/employees/by-department/{department}")
    public ResponseEntity<?> getEmployeesByDepartment(@PathVariable Department department) {
        return ResponseEntity.ok(userRepository.findAllByDepartment(department));
    }


    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/filter")
    public ResponseEntity<?> filterUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department // tipul ca String aici
    ) {
        List<UserDTO> users = userRepository.findAll().stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .filter(user -> name == null || (user.getName() != null && user.getName().toLowerCase().contains(name.toLowerCase())))

                .filter(user -> {
                    if (department == null || department.isBlank()) {
                        return true; // 🔁 fără filtru pe departament
                    }
                    try {
                        return user.getDepartment() == Department.valueOf(department);
                    } catch (IllegalArgumentException e) {
                        return false; // în caz că nu e valid
                    }
                })
                .map(User::getUserDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody EmployeeCreateRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        User user = optionalUser.get();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setDepartment(request.getDepartment());

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }






}
