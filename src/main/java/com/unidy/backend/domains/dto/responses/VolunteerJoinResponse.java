package com.unidy.backend.domains.dto.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerJoinResponse {
    private int userId;
    private String fullName;
    private String workLocation;
    private int age;
}
