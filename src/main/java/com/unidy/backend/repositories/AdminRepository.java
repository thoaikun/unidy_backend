package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Admin;
import com.unidy.backend.domains.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmail(String email);
}
