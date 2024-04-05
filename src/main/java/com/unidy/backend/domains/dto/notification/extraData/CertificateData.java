package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.unidy.backend.domains.entity.Campaign;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateData implements ExtraData {
    private String type = "campaignDetail";
    private Campaign campaign;

    public CertificateData(Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public void accept(ExtraDataVisitor visitor, Message.Builder messageBuilder) {
        visitor.visitCertificateWithSingleMessage(this, messageBuilder);
    }

    @Override
    public void accept(ExtraDataVisitor visitor, MulticastMessage.Builder multicastMessageBuilder) {
        visitor.visitCertificateWithMultiMessage(this, multicastMessageBuilder);
    }
}
