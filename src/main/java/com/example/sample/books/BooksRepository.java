package com.example.sample.books;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BooksRepository extends CrudRepository<Books,Long> {
    @Query("select b from Books b where b.id = ?1 and b.borrow = ?2")
    Optional<Books> findBookById(Long id, boolean isBorrow);
}