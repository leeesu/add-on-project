package com.onpurple.repository;

import com.onpurple.model.ChatMessage;
import com.onpurple.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    List<ChatMessage> findAllByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

}
