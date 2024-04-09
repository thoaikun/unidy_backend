package com.unidy.backend.repositories;

import java.util.List;
import java.util.Optional;

import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.role.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  User findByUserId(int userId);

  List<User> getUsersByRole(Role role, Pageable pageable);
}
