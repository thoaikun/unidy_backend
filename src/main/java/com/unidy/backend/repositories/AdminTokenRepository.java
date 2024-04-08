package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Admin;
import com.unidy.backend.domains.entity.AdminToken;
import com.unidy.backend.domains.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminTokenRepository extends JpaRepository<AdminToken, Integer> {
    Optional<AdminToken> findByToken(String jwt);

    @Query(value = "select t from AdminToken t inner join Admin u on t.admin.admin_id = u.admin_id where u.admin_id = :id and (t.expired = false or t.revoked = false)")
    List<AdminToken> findAllValidTokenByUser(Integer id);
}