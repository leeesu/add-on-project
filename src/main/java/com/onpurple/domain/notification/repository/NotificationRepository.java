package com.onpurple.domain.notification.repository;
import com.onpurple.domain.notification.model.Notification;
import com.onpurple.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findAllByReceiver(User user);
    List<Notification> findAllByIsReadAndReceiver(boolean isRead, User user);

}