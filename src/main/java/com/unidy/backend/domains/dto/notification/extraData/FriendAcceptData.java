package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendAcceptData implements ExtraData {
    final String type = "friendAccept";
    int acceptorId;
    String acceptorName;

    @Override
    public void accept(ExtraDataVisitor visitor, Message.Builder messageBuilder) {
        visitor.visitFriendAcceptWithSingleMessage(this, messageBuilder);
    }

    @Override
    public void accept(ExtraDataVisitor visitor, MulticastMessage.Builder multicastMessageBuilder) {
        visitor.visitFriendAcceptWithMultiMessage(this, multicastMessageBuilder);
    }
}
