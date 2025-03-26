package com.andreeacondrache.licenta.services.employee;

import com.andreeacondrache.licenta.dtos.CommentDTO;
import com.andreeacondrache.licenta.dtos.TaskDTO;
import java.util.List;
public interface EmployeeService {
    List<TaskDTO> getTasksByUserId(Long id);

    TaskDTO updateTask(Long id, String status);

    TaskDTO getTaskById(Long id);

    CommentDTO createComment(Long taskId, Long postedBy, String content);

     List<CommentDTO> getCommentsByTask(Long taskId);


}
