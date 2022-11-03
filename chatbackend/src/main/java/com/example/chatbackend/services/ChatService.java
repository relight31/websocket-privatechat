package com.example.chatbackend.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chatbackend.models.MessageEntity;
import com.example.chatbackend.repositories.ChatRepository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepo;

    public void saveToHistory(MessageEntity msg, String chatId) {
        chatRepo.saveToHistory(msg, chatId);
    }

    public List<MessageEntity> getMessages(String chatId) {
        return chatRepo.getMessages(chatId);
    }

    public JsonArray messagesToJsonArray(List<MessageEntity> messages) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (MessageEntity message : messages) {
            // create jsonobject, push into arraybuilder
            arrayBuilder.add(message.toJsonObject());
        }
        return arrayBuilder.build();
    }
}
