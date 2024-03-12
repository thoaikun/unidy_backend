package com.unidy.backend.domains.dto.responses;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MomoResponse {
    String partnerCode;
    String requestId;
    String orderId;
    Long amount;
    Long responseTime;
    String message;
    Integer resultCode;
    String payUrl;
    @Nullable
    String deeplink;
    @Nullable
    String qrCodeUrl;
    @Nullable
    String deeplinkMiniApp;
    Long transId;
    String signature;
}
