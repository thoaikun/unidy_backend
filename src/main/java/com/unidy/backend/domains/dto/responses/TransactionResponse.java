package com.unidy.backend.domains.dto.responses;
import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.entity.Campaign;
import jakarta.persistence.JoinColumn;
import lombok.*;

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
        int transactionId,
        String transactionType,
        Date transactionTime,
        Long transactionAmount,
        String transactionCode,
        String signature,
        int organizationUserId,
        int campaignId,
        Campaign campaign,
        int userId,
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
        this.user.linkImage = linkImages;
    }
}
