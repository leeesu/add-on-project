package com.onpurple.domain.chatMessage.repository;

import com.onpurple.domain.chatMessage.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop100ByRoomIdOrderByCreatedAtAsc(String roomId);

    ChatMessage findTopByRoomIdOrderByCreatedAtDesc(String roomId);
}