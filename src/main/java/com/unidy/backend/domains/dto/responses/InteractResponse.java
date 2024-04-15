package com.unidy.backend.domains.dto.responses;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InteractResponse {
    String message;
    int totalLike;
}
