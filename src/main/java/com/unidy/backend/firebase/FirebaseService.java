package com.unidy.backend.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.UserDeviceFcmToken;
import com.unidy.backend.repositories.UserDeviceFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class FirebaseService {
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

    public void pushNotification(String deviceToken, String title, String message) {
        try {
            Message notification = Message.builder()
                    .putData("title", title)
                    .putData("message", message)
                    .setToken(deviceToken)
                    .build();
            String response = FirebaseMessaging.getInstance().send(notification);
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
    }

    public void pushNotificationToTopic(String topic, String title, String message) {
        try {
            Message notification = Message.builder()
                    .putData("title", title)
                    .putData("message", message)
                    .setTopic(topic)
                    .build();
            String response = FirebaseMessaging.getInstance().send(notification);
        }
        catch (FirebaseMessagingException error) {
            throw new RuntimeException(error);
        }
    }
}
