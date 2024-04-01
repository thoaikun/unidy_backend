package com.unidy.backend.domains.dto.responses;
import com.unidy.backend.domains.entity.Campaign;
import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.core.env.Environment;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResponse {
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

    private int transactionId;
    private String transactionType;
    private Date transactionTime;
    private Long transactionAmount;
    private String transactionCode;
    private String signature;
    private int organizationUserId;
    private int campaignId;
    private Campaign campaign;
    private UserInfo user;

    public TransactionResponse(
        Integer transactionId,
        String transactionType,
        Date transactionTime,
        Long transactionAmount,
        String transactionCode,
        String signature,
        Integer organizationUserId,
        Integer campaignId,
        Campaign campaign,
        Integer userId,
        String fullName,
        String linkImages
    ) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
        this.transactionAmount = transactionAmount;
        this.transactionCode = transactionCode;
        this.signature = signature;
        this.organizationUserId = organizationUserId;
        this.campaignId = campaignId;
        this.campaign = campaign;
        this.user = new UserInfo();
        this.user.userId = userId;
        this.user.fullName = fullName;
        this.user.linkImage = linkImages == null ? null : "https://unidy.s3.ap-southeast-1.amazonaws.com/" + "profile-images/" + userId + "/" + linkImages;
    }

    public TransactionResponse(
            Long transactionAmount,
            int organizationUserId,
            int campaignId,
            int userId,
            String fullName,
            String linkImages
    ) {
        this.transactionAmount = transactionAmount;
        this.organizationUserId = organizationUserId;
        this.campaignId = campaignId;
        this.user = new UserInfo();
        this.user.userId = userId;
        this.user.fullName = fullName;
        this.user.linkImage = linkImages == null ? null : "https://unidy.s3.ap-southeast-1.amazonaws.com/" + "profile-images/" + userId + "/" + linkImages;
    }
}
