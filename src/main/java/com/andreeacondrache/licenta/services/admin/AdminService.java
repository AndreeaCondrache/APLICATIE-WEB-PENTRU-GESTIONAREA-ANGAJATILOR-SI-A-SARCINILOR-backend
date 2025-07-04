package com.andreeacondrache.licenta.services.admin;

import com.andreeacondrache.licenta.dtos.CommentDTO;
import com.andreeacondrache.licenta.dtos.TaskDTO;
import com.andreeacondrache.licenta.dtos.UserDTO;

import java.util.List;
import java.util.Map;

public interface AdminService {

    List<UserDTO> getUsers();

    TaskDTO postTask(TaskDTO taskDTO);

    List<TaskDTO> getTasks();

    TaskDTO getTaskById(Long id);
    List<TaskDTO> getTasksByUserId(Long userId);


    void deleteTask(Long id);

    List<TaskDTO> searchTask(String title);

    TaskDTO updateTask(Long id, TaskDTO taskDTO);

    CommentDTO createComment(Long taskId, Long postedBy, String content);

    List<CommentDTO> getCommentsByTask(Long taskId);

    void deleteComment(Long id);
    List<Map<String, Object>> getTopProductivity(String period, String department, Long employeeId);

    long countCompletedTasks(String period, String department, Long employeeId);

}
