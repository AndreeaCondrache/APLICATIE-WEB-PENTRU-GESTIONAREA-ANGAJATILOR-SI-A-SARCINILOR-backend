package com.andreeacondrache.licenta.repositories;

import com.andreeacondrache.licenta.entities.Task;
import com.andreeacondrache.licenta.enums.Priority;
import com.andreeacondrache.licenta.enums.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByTitleContaining(String title);
   List<Task> findAllByUserId(Long id);
    List<Task> findByUserIdAndDueDate(Long userId, LocalDate dueDate);
    List<Task> findByUserIdAndDueDateBeforeAndTaskStatusNot(Long userId, LocalDate date, TaskStatus status);
    List<Task> findByUserId(Long userId);
    Optional<Task> findFirstByUserIdOrderByDueDateAsc(Long userId);
    List<Task> findByUserIdAndTaskStatus(Long userId, TaskStatus status);


    List<Task> findByUserIdAndPriority(Long userId, Priority foundPriority);
}
