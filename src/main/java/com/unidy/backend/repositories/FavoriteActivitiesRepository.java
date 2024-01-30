package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.FavoriteActivities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteActivitiesRepository extends JpaRepository<FavoriteActivities,Integer> {

    FavoriteActivities findByUserId(Integer userId);
}
