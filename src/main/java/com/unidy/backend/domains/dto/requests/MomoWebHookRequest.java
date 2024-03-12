package com.unidy.backend.domains.dto.requests;

import jakarta.annotation.Nullable;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoWebHookRequest {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private String orderInfo;
    @Nullable
    private String partnerUserId;
    private String orderType;
    private Long transId;
    private Integer resultCode;
    private String message;
    private String payType;
    private Long responseTime;
    private String extraData;
    private String signature;
    @Nullable
    private String paymentOption;
}
