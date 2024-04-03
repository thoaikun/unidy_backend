package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateData implements ExtraData {
    private int campaignId;
    private String campaignName;
    private int organizationId;
    private String organizationName;
    private String VolunteerName;
    private String certificateLink;
    @Override
    public void accept(ExtraDataVisitor visitor, Message.Builder messageBuilder) {
        visitor.visitCertificateWithSingleMessage(this, messageBuilder);
    }

    @Override
    public void accept(ExtraDataVisitor visitor, MulticastMessage.Builder multicastMessageBuilder) {
        visitor.visitCertificateWithMultiMessage(this, multicastMessageBuilder);
    }
}
