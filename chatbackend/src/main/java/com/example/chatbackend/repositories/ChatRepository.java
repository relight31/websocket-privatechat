package com.example.chatbackend.repositories;

import static com.example.chatbackend.models.MessageEntity.rowSetToMessageEntity;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.example.chatbackend.models.MessageEntity;

@Repository
public class ChatRepository {

    @Autowired
    private JdbcTemplate template;

    private final String SQL_GET_MESSAGES_BY_CHATID = "select * from messages where chat_id = ?";
    private final String SQL_SAVE_MESSAGE = "insert into messages (chat_id, sender, content, msg_timestamp) values (?,?,?,?)";

    public void saveToHistory(MessageEntity message, String chatId) {
        template.update(SQL_SAVE_MESSAGE,
                message.getChatId(),
                message.getSender(),
                message.getContent(),
                message.getTimestamp());
    }

    public List<MessageEntity> getMessages(String chatId) {
        List<MessageEntity> messages = new LinkedList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_MESSAGES_BY_CHATID, chatId);
        while (rowSet.next()) {
            messages.add(rowSetToMessageEntity(rowSet));
        }
        return messages;
    }
}
