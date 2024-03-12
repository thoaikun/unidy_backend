package com.unidy.backend.domains.dto.responses;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResponse {
    private int transactionId;
    private String transactionType;
    private Date transactionTime;
    private Long transactionAmount;
    private String transactionCode;
    private Integer userId;
    private String fullName;
    private String email;
    private Integer campaignId;
    private String content;
    private Integer donationBudgetReceived;
    private Integer donationBudget;
}
