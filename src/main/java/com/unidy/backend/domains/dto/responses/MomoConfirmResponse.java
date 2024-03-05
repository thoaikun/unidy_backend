package com.unidy.backend.domains.dto.responses;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoConfirmResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private Long amount;
    private Long transId;
    private Integer resultCode;
    private String message;
    private String requestType;
    private Long responseTime;
}
