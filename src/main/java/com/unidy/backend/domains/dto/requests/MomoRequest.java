package com.unidy.backend.domains.dto.requests;

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
    String accessKey;
    String secretKey;
    String requestId;
    Long amount;
    String orderId;
    String orderInfo;
    Boolean autoCapture;
    String redirectUrl;
    String ipnUrl;
    String extraData;
    String requestType;
    String storeId;
    String signature;
    String lang;
}