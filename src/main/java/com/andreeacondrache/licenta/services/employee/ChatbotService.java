package com.andreeacondrache.licenta.services.employee;

import com.andreeacondrache.licenta.dtos.ChatMessage;
import com.andreeacondrache.licenta.dtos.ChatResponse;
import com.andreeacondrache.licenta.dtos.TaskDTO;
import com.andreeacondrache.licenta.entities.Task;
import com.andreeacondrache.licenta.enums.Priority;
import com.andreeacondrache.licenta.enums.TaskStatus;
import com.andreeacondrache.licenta.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final TaskRepository taskRepository;

    public ChatResponse handleMessage(String input, Long userId) {
        input = input.toLowerCase().trim();
        ChatResponse response = new ChatResponse();

        if (input.contains("due tomorrow")) {
            List<Task> tasks = taskRepository.findByUserIdAndDueDate
                    (userId, LocalDate.now().plusDays(1));
            response.setReply("Here are your tasks due tomorrow:");
            response.setTasks(tasks.stream().map(Task::getTaskDTO).toList());
            response.setSuccess(true);

        } else if (input.contains("overdue")) {
            List<Task> tasks = taskRepository.findByUserIdAndDueDateBeforeAndTaskStatusNot(userId, LocalDate.now(), TaskStatus.COMPLETED);
            response.setReply("Here are your overdue tasks:");
            response.setTasks(tasks.stream().map(Task::getTaskDTO).toList());
            response.setSuccess(true);

        } else if (input.contains("this week")) {
            LocalDate today = LocalDate.now();
            LocalDate endOfWeek = today.plusDays(7);
            List<TaskDTO> tasks = taskRepository.findByUserId(userId).stream()
                    .filter(task -> {
                        LocalDate taskDue = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return !taskDue.isBefore(today) && !taskDue.isAfter(endOfWeek);
                    })
                    .map(Task::getTaskDTO)
                    .toList();
            response.setReply("Here are your tasks for this week:");
            response.setTasks(tasks);
            response.setSuccess(true);

        } else if (input.contains("next task")) {
            Optional<Task> taskOpt = taskRepository.findFirstByUserIdOrderByDueDateAsc(userId);
            if (taskOpt.isPresent()) {
                response.setReply("Your next upcoming task is:");
                response.setTasks(List.of(taskOpt.get().getTaskDTO()));
                response.setSuccess(true);
            } else {
                response.setReply("You have no upcoming tasks.");
                response.setSuccess(false);
            }

        } else if (input.contains("in progress")) {
            List<Task> tasks = taskRepository.findByUserIdAndTaskStatus(userId,
                    TaskStatus.INPROGRESS);
            response.setReply("Here are your tasks in progress:");
            response.setTasks(tasks.stream().map(Task::getTaskDTO).toList());
            response.setSuccess(true);

        } else if (input.contains("priority")) {
            Priority foundPriority = null;
            if (input.contains("high")) {
                foundPriority = Priority.HIGH;
            } else if (input.contains("medium")) {
                foundPriority = Priority.MEDIUM;
            } else if (input.contains("low")) {
                foundPriority = Priority.LOW;
            }

            if (foundPriority != null) {
                List<Task> tasks = taskRepository.findByUserIdAndPriority(userId, foundPriority);
                response.setReply("Here are your " + foundPriority.name().toLowerCase() + " priority tasks:");
                response.setTasks(tasks.stream().map(Task::getTaskDTO).toList());
                response.setSuccess(true);
            } else {
                response.setReply("Please specify a valid priority: low, medium, or high.");
                response.setSuccess(false);
            }

        } else if (input.contains("mark task") && input.contains("complete")) {
            try {
                String[] words = input.split(" ");
                Long taskId = null;
                for (String word : words) {
                    if (word.matches("\\d+")) {
                        taskId = Long.parseLong(word);
                        break;
                    }
                }

                if (taskId != null) {
                    Optional<Task> taskOpt = taskRepository.findById(taskId);
                    if (taskOpt.isPresent() && taskOpt.get().getUser().getId().equals(userId)) {
                        Task task = taskOpt.get();
                        task.setTaskStatus(TaskStatus.COMPLETED);
                        taskRepository.save(task);
                        response.setReply("Task #" + taskId + " marked as completed.");
                        response.setSuccess(true);
                    } else {
                        response.setReply("Task not found or does not belong to you.");
                        response.setSuccess(false);
                    }
                } else {
                    response.setReply("Couldn't find a valid task ID to mark as complete.");
                    response.setSuccess(false);
                }

            } catch (Exception e) {
                response.setReply("Error processing your request.");
                response.setSuccess(false);
            }

        } else {
            response.setReply("Sorry, I didn't understand. Try asking things like:\n" +
                    "- What are my tasks this week?\n" +
                    "- Show my high priority tasks\n" +
                    "- Mark task 12 as complete");
            response.setSuccess(false);
        }

        return response;
    }
}
