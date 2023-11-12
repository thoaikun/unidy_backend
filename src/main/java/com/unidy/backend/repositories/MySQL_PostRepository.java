package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface MySQL_PostRepository extends JpaRepository<Post, Integer> {
}
