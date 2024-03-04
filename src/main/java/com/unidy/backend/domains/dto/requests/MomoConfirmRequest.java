package com.unidy.backend.domains.dto.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MomoConfirmRequest {
    String partnerCode;
    String requestId;
    String orderId;
    String requestType;
    String lang;
    Long amount;
    String description;
    String signature;
}
