package com.project.date.repository;

import com.project.date.model.ChatMessage;
import com.project.date.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    List<ChatMessage> findAllByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}
