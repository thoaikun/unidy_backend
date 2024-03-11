package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

public interface ExtraData {
    void accept(ExtraDataVisitor visitor, Message.Builder messageBuilder);
    void accept(ExtraDataVisitor visitor, MulticastMessage.Builder multicastMessageBuilder);
}