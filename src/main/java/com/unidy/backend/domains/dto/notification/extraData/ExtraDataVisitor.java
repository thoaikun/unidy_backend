package com.unidy.backend.domains.dto.notification.extraData;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

public class ExtraDataVisitor {
    public void visitFriendAcceptWithSingleMessage(FriendAcceptData friendAcceptData, Message.Builder messageBuilder) {
        messageBuilder.putData("type", friendAcceptData.getType());
        messageBuilder.putData("acceptorId", String.valueOf(friendAcceptData.getAcceptorId()));
        messageBuilder.putData("acceptorName", friendAcceptData.getAcceptorName());
    }

    public void visitFriendRequestWithSingleMessage(FriendRequestData friendRequestData, Message.Builder messageBuilder) {
        messageBuilder.putData("type", friendRequestData.getType());
        messageBuilder.putData("requestorId", String.valueOf(friendRequestData.getRequestorId()));
        messageBuilder.putData("requestorName", friendRequestData.getRequestorName());
    }

    public void visitFriendAcceptWithMultiMessage(FriendAcceptData friendAcceptData, MulticastMessage.Builder multicastMessageBuilder) {
        multicastMessageBuilder.putData("type", friendAcceptData.getType());
        multicastMessageBuilder.putData("acceptorId", String.valueOf(friendAcceptData.getAcceptorId()));
        multicastMessageBuilder.putData("acceptorName", friendAcceptData.getAcceptorName());
    }

    public void visitFriendRequestWithMultiMessage(FriendRequestData friendRequestData, MulticastMessage.Builder multicastMessageBuilder) {
        multicastMessageBuilder.putData("type", friendRequestData.getType());
        multicastMessageBuilder.putData("requestorId", String.valueOf(friendRequestData.getRequestorId()));
        multicastMessageBuilder.putData("requestorName", friendRequestData.getRequestorName());
    }

    public void visitNewCampaignWithSingleMessage(NewCampaignData newCampaignData, Message.Builder messageBuilder) {
        messageBuilder.putData("type", newCampaignData.getType());
        messageBuilder.putData("campaignId", String.valueOf(newCampaignData.getCampaignId()));
        messageBuilder.putData("campaignName", newCampaignData.getCampaignName());
    }

    public void visitNewCampaignWithMultiMessage(NewCampaignData newCampaignData, MulticastMessage.Builder multicastMessageBuilder) {
        multicastMessageBuilder.putData("type", newCampaignData.getType());
        multicastMessageBuilder.putData("campaignId", String.valueOf(newCampaignData.getCampaignId()));
        multicastMessageBuilder.putData("campaignName", newCampaignData.getCampaignName());
    }

    public void visitCertificateWithSingleMessage(CertificateData certificateData, Message.Builder messageBuilder) {
        messageBuilder.putData("campaignId", String.valueOf(certificateData.getCampaignId()));
        messageBuilder.putData("campaignName", certificateData.getCampaignName());
        messageBuilder.putData("organizationId", String.valueOf(certificateData.getOrganizationId()));
        messageBuilder.putData("organizationName", String.valueOf(certificateData.getOrganizationName()));
        messageBuilder.putData("volunteerName", certificateData.getVolunteerName());
        messageBuilder.putData("certificateLink", certificateData.getCertificateLink());

    }
    public void visitCertificateWithMultiMessage(CertificateData certificateData, MulticastMessage.Builder multicastMessageBuilder) {
        multicastMessageBuilder.putData("campaignId", String.valueOf(certificateData.getCampaignId()));
        multicastMessageBuilder.putData("campaignName", certificateData.getCampaignName());
        multicastMessageBuilder.putData("organizationId", String.valueOf(certificateData.getOrganizationId()));
        multicastMessageBuilder.putData("organizationName", String.valueOf(certificateData.getOrganizationName()));
        multicastMessageBuilder.putData("certificateLink", certificateData.getCertificateLink());
    }
}
