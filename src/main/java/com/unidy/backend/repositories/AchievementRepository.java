package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,Integer> {
}
