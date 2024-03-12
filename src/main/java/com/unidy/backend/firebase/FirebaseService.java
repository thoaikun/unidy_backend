package com.unidy.backend.firebase;

import com.google.firebase.messaging.*;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.UserDeviceFcmToken;
import com.unidy.backend.repositories.UserDeviceFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FirebaseService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserDeviceFcmTokenRepository userDeviceFcmTokenRepository;

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

    public void subscribeToTopic(String deviceToken, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(
                    new ArrayList<String>() {{ add(deviceToken); }},
                    topic
            );
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
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
}
