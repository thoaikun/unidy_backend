package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
