package com.andreeacondrache.licenta.services.admin;

import com.andreeacondrache.licenta.dtos.CommentDTO;
import com.andreeacondrache.licenta.dtos.TaskDTO;
import com.andreeacondrache.licenta.dtos.UserDTO;
import com.andreeacondrache.licenta.entities.Comment;
import com.andreeacondrache.licenta.entities.Task;
import com.andreeacondrache.licenta.entities.User;
import com.andreeacondrache.licenta.enums.TaskStatus;
import com.andreeacondrache.licenta.enums.UserRole;
import com.andreeacondrache.licenta.repositories.CommentRepository;
import com.andreeacondrache.licenta.repositories.TaskRepository;
import com.andreeacondrache.licenta.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .map(User::getUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO postTask(TaskDTO taskDTO) {
        Optional<User> optionalUser = userRepository.findById(taskDTO.getEmployeeId());
        if(optionalUser.isPresent()){
            Task task = new Task();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setPriority(taskDTO.getPriority());
            task.setDueDate(taskDTO.getDueDate());
            task.setUser(optionalUser.get());
            task.setTaskStatus(TaskStatus.PENDING);
            System.out.println("Id angajat primit : "+ taskDTO.getEmployeeId());
            return  taskRepository.save(task).getTaskDTO();


        }
        return null;
    }

    @Override
    public List<TaskDTO> getTasks() {
        return taskRepository.findAll().stream().map(Task::getTaskDTO).collect(Collectors.toList());
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.map(Task::getTaskDTO).orElse(null);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskDTO> searchTask(String title) {
        return taskRepository.findAllByTitleContaining(title).stream().map(Task::getTaskDTO).collect(Collectors.toList());

    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
       Optional<Task> optionalTask = taskRepository.findById(id);
       Optional<User> optionalUser = userRepository.findById(taskDTO.getEmployeeId());
       if(optionalTask.isPresent() && optionalUser.isPresent()){
           Task existingTask = optionalTask.get();
           existingTask.setTitle(taskDTO.getTitle());
           existingTask.setDescription(taskDTO.getDescription());
           existingTask.setDueDate(taskDTO.getDueDate());
           existingTask.setPriority(taskDTO.getPriority());
           existingTask.setUser(optionalUser.get());
           TaskStatus taskStatus = mapStringToTaskStatus(String.valueOf(taskDTO.getTaskStatus()));
            existingTask.setTaskStatus(taskStatus);
          return   taskRepository.save(existingTask).getTaskDTO();
       }
        return null;
    }

    @Override
    public CommentDTO createComment(Long taskId, Long postedBy, String content) {
       Optional<Task> optionalTask = taskRepository.findById(taskId);
       Optional<User> optionalUser = userRepository.findById(postedBy);
       if(optionalUser.isPresent() && optionalTask.isPresent()){
           Comment comment = new Comment();
           comment.setContent(content);
           comment.setCreatedAt(new Date());
           comment.setUser(optionalUser.get());
           comment.setTask(optionalTask.get());
           return commentRepository.save(comment).getCommentDTO();
       }
       throw  new EntityNotFoundException("Task or user not found!");
    }

    @Override
    public List<CommentDTO> getCommentsByTask(Long taskId) {
        return commentRepository.findAllByTaskId(taskId).stream().map(Comment::getCommentDTO).collect(Collectors.toList() );
    }

    private TaskStatus mapStringToTaskStatus(String taskStatus){
       return  switch (taskStatus){
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<TaskDTO> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
    }





    @Override
    public List<Map<String,Object>> getTopProductivity(String period, String department, Long employeeId) {
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .filter(user -> department == null || department.isBlank() || user.getDepartment().name().equalsIgnoreCase(department))
                .filter(user -> employeeId == null || user.getId().equals(employeeId))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();

        for (User user : employees) {
            List<Task> userTasks = taskRepository.findByUserId(user.getId());

            // Filtrare după perioadă
            userTasks = filterByPeriod(userTasks, period);

            int total = userTasks.size();
            int completed = (int) userTasks.stream().filter(t -> t.getTaskStatus() == TaskStatus.COMPLETED).count();

            int productivity = total == 0 ? 0 : (int) ((completed * 100.0) / total);

            Map<String, Object> userStats = new HashMap<>();
            userStats.put("name", user.getName());
            userStats.put("percent", productivity);

            result.add(userStats);
        }

        // Sortare descrescătoare
        return result.stream()
                .sorted((a, b) -> ((Integer) b.get("percent")).compareTo((Integer) a.get("percent")))
                .collect(Collectors.toList());
    }

    // Metodă utilitară pentru filtrare după perioadă
    private List<Task> filterByPeriod(List<Task> tasks, String period) {
        if (period == null || period.equals("all")) return tasks;

        LocalDate now = LocalDate.now();
        LocalDate fromDate;

        switch (period) {
            case "week" -> fromDate = now.minusWeeks(1);
            case "month" -> fromDate = now.withDayOfMonth(1);
            case "year" -> fromDate = now.withDayOfYear(1);
            default -> fromDate = now.minusYears(10); // fallback
        }

        Date from = Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return tasks.stream()
                .filter(task -> task.getDueDate() != null && !task.getDueDate().before(from))
                .collect(Collectors.toList());
    }



    public long countCompletedTasks(String period, String department, Long employeeId) {
        LocalDate fromDate = switch (period != null ? period : "") {
            case "week" -> LocalDate.now().minusWeeks(1);
            case "month" -> LocalDate.now().withDayOfMonth(1);
            case "year" -> LocalDate.now().withDayOfYear(1);
            default -> null;
        };

        return taskRepository.findAll().stream()
                .filter(t -> t.getTaskStatus() == TaskStatus.COMPLETED)
                .filter(t -> fromDate == null || !t.getDueDate().toInstant().isBefore(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .filter(t -> department == null || department.isBlank() ||
                        (t.getUser() != null && t.getUser().getDepartment().name().equalsIgnoreCase(department)))
                .filter(t -> employeeId == null || (t.getUser() != null && t.getUser().getId().equals(employeeId)))
                .count();
    }

}
