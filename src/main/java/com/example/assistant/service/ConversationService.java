package com.example.assistant.service;

import com.example.assistant.model.Conversation;
import com.example.assistant.model.Message;
import com.example.assistant.model.User;
import com.example.assistant.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    
    public Conversation createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        return conversationRepository.save(conversation);
    }
    
    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUserOrderByUpdatedAtDesc(user);
    }
    
    public Conversation addMessage(Conversation conversation, Message message) {
        message.setConversation(conversation);
        conversation.getMessages().add(message);
        return conversationRepository.save(conversation);
    }
} 