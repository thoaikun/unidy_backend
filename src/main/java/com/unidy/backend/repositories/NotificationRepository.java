package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.NotificationResponse;
import com.unidy.backend.domains.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    @Query("""
        SELECT new com.unidy.backend.domains.dto.responses.NotificationResponse(
            n.id,
            n.title,
            n.description,
            n.createdTime,
            n.seenTime,
            n.type,
            n.extra,
            n.receiverId,
            u.userId,
            u.fullName,
            upi.linkImage
        )
        FROM Notification n
        JOIN User u ON n.ownerId = u.userId
        LEFT JOIN UserProfileImage upi ON n.ownerId = upi.userId
        WHERE n.receiverId = :receiverId
    """)
    List<NotificationResponse> getNotificationsByReceiverId(int receiverId, Pageable pageable);

    @Query("""
        SELECT COUNT(n)
        FROM Notification n
        WHERE n.receiverId = :receiverId AND n.seenTime IS NULL
    """)
    Integer countByReceiverIdAndSeenTimeIsNull(int receiverId);

    List<Notification> getNotificationsByReceiverIdAndSeenTimeIsNull(Integer userId);
}
