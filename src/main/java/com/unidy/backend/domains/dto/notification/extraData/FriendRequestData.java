package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FriendRequestData implements ExtraData {
    final String type = "friendRequest";
    int requestorId;
    String requestorName;

    @Override
    public void accept(ExtraDataVisitor visitor, Message.Builder messageBuilder) {
        visitor.visitFriendRequestWithSingleMessage(this, messageBuilder);
    }

    @Override
    public void accept(ExtraDataVisitor visitor, MulticastMessage.Builder multicastMessageBuilder) {
        visitor.visitFriendRequestWithMultiMessage(this, multicastMessageBuilder);
    }
}
