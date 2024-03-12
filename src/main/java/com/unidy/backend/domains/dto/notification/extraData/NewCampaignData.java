package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewCampaignData implements ExtraData {
    private final String type = "newCampaign";
    private int campaignId;
    private int organizationId;
    private String campaignName;

    @Override
    public void accept(ExtraDataVisitor visitor, Message.Builder messageBuilder) {
        visitor.visitNewCampaignWithSingleMessage(this, messageBuilder);
    }

    @Override
    public void accept(ExtraDataVisitor visitor, MulticastMessage.Builder multicastMessageBuilder) {
        visitor.visitNewCampaignWithMultiMessage(this, multicastMessageBuilder);
    }
}
