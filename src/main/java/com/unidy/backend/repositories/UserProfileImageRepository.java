package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.UserProfileImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Integer> {
    UserProfileImage findByUserId(int userId);
}
