package com.example.assistant.controller;

import com.example.assistant.model.Conversation;
import com.example.assistant.model.Message;
import com.example.assistant.model.User;
import com.example.assistant.service.ConversationService;
import com.example.assistant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/{username}")
    public ResponseEntity<?> createConversation(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> {
                    Conversation conversation = conversationService.createConversation(user);
                    return ResponseEntity.ok(conversation);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserConversations(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> {
                    List<Conversation> conversations = conversationService.getUserConversations(user);
                    return ResponseEntity.ok(conversations);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<?> addMessage(@PathVariable Long conversationId, @RequestBody Message message) {
        // TODO: 实现消息添加逻辑
        return ResponseEntity.ok().build();
    }
} 