package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.UserDeviceFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceFcmTokenRepository extends JpaRepository<UserDeviceFcmToken,Integer> {
    @Query("SELECT u FROM UserDeviceFcmToken u WHERE u.fcmToken = ?1")
    UserDeviceFcmToken findByFcmToken(String fcmToken);
}
