package com.unidy.backend.firebase;

import com.google.api.client.util.DateTime;
import com.google.firebase.messaging.*;
import com.unidy.backend.domains.Type.NotificationType;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.entity.Notification;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.UserDeviceFcmToken;
import com.unidy.backend.repositories.NotificationRepository;
import com.unidy.backend.repositories.UserDeviceFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FirebaseService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserDeviceFcmTokenRepository userDeviceFcmTokenRepository;
    private final NotificationRepository notificationRepository;

    public void saveFcmToken(String fcmToken, Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            UserDeviceFcmToken userDeviceFcmToken = userDeviceFcmTokenRepository.findByFcmToken(fcmToken);
            if (userDeviceFcmToken != null) {
                userDeviceFcmToken.setUserId(user.getUserId());
                userDeviceFcmTokenRepository.save(userDeviceFcmToken);
                return;
            }
            userDeviceFcmToken = UserDeviceFcmToken.builder()
                    .userId(user.getUserId())
                    .fcmToken(fcmToken)
                    .build();
            userDeviceFcmTokenRepository.save(userDeviceFcmToken);
        }
        catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public void removeFcmToken(String fcmToken) {
        try {
            userDeviceFcmTokenRepository.deleteByFcmToken(fcmToken);
        }
        catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public void pushNotification(NotificationDto notificationDto) {
        try {
            Message message = notificationDto.toFirebaseMessage();
            String response = firebaseMessaging.send(message);
        }
        catch (FirebaseMessagingException error) {
            System.out.println(error.getMessage());
        }
    }

    public void pushNotificationToTopic(NotificationDto notificationDto) throws FirebaseMessagingException {
        Message message = notificationDto.toFirebaseMessage();
        String response =firebaseMessaging.send(message);
    }

    public void pushNotificationToMultipleDevices(NotificationDto notificationDto) throws FirebaseMessagingException {
         MulticastMessage multicastMessage = notificationDto.toFirebaseMulticastMessage();
         BatchResponse response = firebaseMessaging.sendMulticast(multicastMessage);
    }

    public void unsubscribeFromTopic(String deviceToken, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(
                    new ArrayList<String>() {{ add(deviceToken); }},
                    topic
            );
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
    }

    public void saveNotification(int ownerId, int receiverId, NotificationType type,
                                 String title, String description, String extra) {
        try {
            Notification notification = Notification.builder()
                    .ownerId(ownerId)
                    .receiverId(receiverId)
                    .type(type.toString())
                    .title(title)
                    .description(description)
                    .extra(extra)
                    .build();
            notificationRepository.save(notification);
        }
        catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public void saveNotification(int ownerId, List<Integer> receiverIds, NotificationType type,
                                 String title, String description, String extra) {
        try {
            List<Notification> notifications = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int receiverId : receiverIds) {
                Notification notification = Notification.builder()
                        .ownerId(ownerId)
                        .receiverId(receiverId)
                        .type(type.toString())
                        .title(title)
                        .description(description)
                        .extra(extra)
                        .build();
                notifications.add(notification);
            }
            notificationRepository.saveAll(notifications);
        }
        catch (Exception error) {
            throw new RuntimeException(error);
        }
    }
}
