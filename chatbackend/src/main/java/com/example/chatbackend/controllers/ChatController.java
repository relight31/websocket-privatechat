package com.example.chatbackend.controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chatbackend.models.MessageEntity;
import com.example.chatbackend.services.ChatService;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ChatService chatSvc;

    @MessageMapping("/chat/{chatId}")
    public void chat(
            @DestinationVariable String chatId,
            @Payload MessageEntity msg) {
        // make timestamp
        Timestamp timestamp = Timestamp.from(Instant.now());
        msg.setTimestamp(timestamp.toString());
        // save message into database for chat history
        chatSvc.saveToHistory(msg, chatId);
        // put message in websocket
        template.convertAndSend("/topic/messages/" + chatId, msg);
    }

    @GetMapping(path = "/getMessages")
    public ResponseEntity<String> getMessages(@RequestParam String chatId) {
        // get messages from DB
        List<MessageEntity> messages = chatSvc.getMessages(chatId);
        // return as list of messages
        return ResponseEntity.ok(
                chatSvc.messagesToJsonArray(messages).toString());
    }

}
