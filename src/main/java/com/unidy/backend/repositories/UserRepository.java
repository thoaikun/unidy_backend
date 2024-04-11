package com.unidy.backend.repositories;

import java.util.List;
import java.util.Optional;

import com.unidy.backend.domains.dto.responses.UserInfoForAdminResponse;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  User findByUserId(int userId);

  Page<User> getUsersByRole(Role role, Pageable pageable);

  @Query("""
    SELECT new com.unidy.backend.domains.dto.responses.UserInfoForAdminResponse(
          u.userId,
          u.fullName,
          u.address,
          u.dayOfBirth,
          u.sex,
          u.phone,
          u.email,
          u.job,
          u.workLocation,
          u.role,
          u.isBlock,
          upi.linkImage
    )
    FROM User u
    LEFT JOIN UserProfileImage upi
        ON u.userId = upi.userId
    WHERE u.userId = :volunteerId
  """)
  UserInfoForAdminResponse getUserInfoForAdminById(int volunteerId);

  @Query("""
    SELECT new com.unidy.backend.domains.dto.responses.UserInfoForAdminResponse(
      u.userId,
      u.fullName,
      u.address,
      u.dayOfBirth,
      u.sex,
      u.phone,
      u.email,
      u.job,
      u.workLocation,
      u.role,
      u.isBlock,
      upi.linkImage
    )
    FROM User u
    LEFT JOIN UserProfileImage upi
        ON u.userId = upi.userId
    WHERE u.role = :role
  """)
  Page<UserInfoForAdminResponse> getAllUserInfoForAdminByRole(Role role, Pageable pageable);
}
