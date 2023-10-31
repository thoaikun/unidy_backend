package com.unidy.backend.repositories;

import java.util.Optional;

import com.unidy.backend.domains.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  User findByUserId(int userId);
}
