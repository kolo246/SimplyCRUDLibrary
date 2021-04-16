package com.example.sample.books;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BooksPagingRepository extends PagingAndSortingRepository<Books,Long> {
    List<Books> findAll(PageRequest pageRequest);
}
