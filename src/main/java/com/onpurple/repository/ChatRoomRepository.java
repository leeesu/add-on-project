package com.onpurple.repository;

import com.onpurple.model.ChatRoom;
import com.onpurple.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserAndOtherUser(User user, User otherUser);
    List<ChatRoom> findAllByUserOrOtherUser(User user, User otherUser);

    Optional<ChatRoom> findByUserOrOtherUser(User user, User OtherUser);
}
