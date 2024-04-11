package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Transactional
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> getPostsByCreateDateBetween(Date startDate, Date endDate, Pageable pageable);

    Post getPostByPostId(String postId);
}
