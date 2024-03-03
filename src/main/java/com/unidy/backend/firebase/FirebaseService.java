package com.unidy.backend.firebase;

import com.google.firebase.messaging.*;
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

    public void pushNotification(String deviceToken, String title, String body) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setNotification(notification)
                    .setToken(deviceToken)
                    .build();
            String response = firebaseMessaging.send(message);
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
    }

    public void pushNotificationToTopic(String topic, String title, String body) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setNotification(notification)
                    .setTopic(topic)
                    .build();
            String response =firebaseMessaging.send(message);
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
    }

    public void pushNotificationToMultipleDevices(ArrayList<String> deviceTokens, String title, String body) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(deviceTokens)
                    .build();

            BatchResponse response = firebaseMessaging.sendMulticast(multicastMessage);
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
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
