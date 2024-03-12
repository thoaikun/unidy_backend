package com.unidy.backend.domains.dto.notification;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.unidy.backend.domains.dto.notification.extraData.ExtraData;
import com.unidy.backend.domains.dto.notification.extraData.ExtraDataVisitor;
import lombok.*;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private String title;
    private String body;
    @Nullable
    private String topic;
    @Nullable
    private ExtraData extraData;
    @Nullable
    private ArrayList<String> deviceTokens;
    @Nullable
    private String deviceToken;

    public Message toFirebaseMessage() {
        Message.Builder messageBuilder = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(this.title)
                        .setBody(this.body)
                        .build());
        if (this.topic != null) {
            messageBuilder.setTopic(this.topic);
        }
        if (this.deviceToken != null) {
            messageBuilder.setToken(this.deviceToken);
        }
        if (this.extraData != null) {
            this.extraData.accept(new ExtraDataVisitor(), messageBuilder);
        }

        return messageBuilder.build();
    }

    public MulticastMessage toFirebaseMulticastMessage() {
        MulticastMessage.Builder multicastMessageBuilder = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(this.title)
                        .setBody(this.body)
                        .build());
        if (this.deviceTokens != null) {
            multicastMessageBuilder.addAllTokens(this.deviceTokens);
        }
        if (this.extraData != null) {
            this.extraData.accept(new ExtraDataVisitor(), multicastMessageBuilder);
        }

        return multicastMessageBuilder.build();
    }
}



