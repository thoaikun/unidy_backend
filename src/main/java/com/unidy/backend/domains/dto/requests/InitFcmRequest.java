package com.unidy.backend.domains.dto.requests;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitFcmRequest {
    String fcmToken;
}
