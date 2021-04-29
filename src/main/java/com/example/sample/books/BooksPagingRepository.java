package com.example.sample.books;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BooksPagingRepository extends PagingAndSortingRepository<Books, Long> {
    @Query("select b from Books b order by b.id")
    List<Books> findAll(PageRequest pageRequest);

    @Query("select b from Books b where (b.borrow = :isBorrow or :isBorrow is null) ")
    List<Books> findAllByBorrowIsFalse(@Param("isBorrow") boolean isBorrow, Pageable pageable);
}