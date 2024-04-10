package com.unidy.backend.domains.dto.responses;

import com.google.api.client.util.DateTime;
import com.google.gson.Gson;
import com.unidy.backend.domains.Type.NotificationType;
import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    @Data
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static private class UserInfo {
        private int userId;
        private String fullName;
        private String linkImage;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    static public class UnseenCountResponse {
        private int unseenCount;
    }

    @Data
    @Setter
    @Getter
    static private class NotificationExtra {
        private String id;
    }

    private int notificationId;
    private String title;
    private String description;
    private Timestamp createdTime;
    private Timestamp seenTime;
    private String type;
    private NotificationExtra extra;
    private int receiver;
    private UserInfo owner;

    public NotificationResponse(
        int notificationId,
        String title,
        String description,
        Timestamp createdTime,
        Timestamp seenTime,
        String type,
        String extra,
        Integer receiverId,
        Integer ownerId,
        String ownerFullName,
        String ownerLinkImages
    ) {
        this.notificationId = notificationId;
        this.title = title;
        this.description = description;
        this.createdTime = createdTime;
        this.seenTime = seenTime;
        this.type = type;
        this.extra = new Gson().fromJson(extra, NotificationExtra.class);
        this.receiver = receiverId;
        ownerLinkImages = ownerLinkImages;
        this.owner = new UserInfo(ownerId, ownerFullName, ownerLinkImages);
    }
}
