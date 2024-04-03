package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.UserDeviceFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDeviceFcmTokenRepository extends JpaRepository<UserDeviceFcmToken,Integer> {
    @Query("SELECT u FROM UserDeviceFcmToken u WHERE u.fcmToken = ?1")
    UserDeviceFcmToken findByFcmToken(String fcmToken);

    @Query("DELETE FROM UserDeviceFcmToken u WHERE u.fcmToken = ?1")
    void deleteByFcmToken(String fcmToken);

    List<UserDeviceFcmToken> findByUserId(Integer userId);
}
