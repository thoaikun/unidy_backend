package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {
    Volunteer findByUserId(int userId);
}
