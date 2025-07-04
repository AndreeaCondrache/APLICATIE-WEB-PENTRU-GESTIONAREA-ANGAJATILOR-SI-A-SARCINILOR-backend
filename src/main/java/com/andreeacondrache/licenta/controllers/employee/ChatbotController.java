package com.andreeacondrache.licenta.controllers.employee;

import com.andreeacondrache.licenta.dtos.ChatMessage;
import com.andreeacondrache.licenta.dtos.ChatResponse;
import com.andreeacondrache.licenta.services.employee.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatResponse> handleChat(@RequestBody ChatMessage message) {
        ChatResponse response = chatbotService.handleMessage(message.getContent(), message.getUserId());
        return ResponseEntity.ok(response);
    }
}
