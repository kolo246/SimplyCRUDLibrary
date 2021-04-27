package com.example.sample.books;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BooksRepository extends CrudRepository<Books,Long> {
    Optional<Books> findBooksByAuthor(String author);
}