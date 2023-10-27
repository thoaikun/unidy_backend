package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
