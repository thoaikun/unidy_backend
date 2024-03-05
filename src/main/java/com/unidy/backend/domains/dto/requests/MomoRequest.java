package com.unidy.backend.domains.dto.requests;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class MomoRequest {
    String partnerCode;
    @Nullable
    String subPartnerCode;
    @Nullable
    String storeName;
    @Nullable
    String storeId;
    Long amount;
    String orderId;
    String orderInfo;
    @Nullable
    String orderGroupId;
    String redirectUrl;
    String requestId;
    String ipnUrl;
    String requestType;
    String extraData;
    String accessKey;
    String secretKey;
    Boolean autoCapture;
    String lang;
    String signature;
}