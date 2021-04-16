package com.example.sample.books;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BooksPagingRepository extends PagingAndSortingRepository<Books,Long> {
    @Query("select b from Books b order by b.id")
    List<Books> findAll(PageRequest pageRequest);
    @Query("select b from Books b where b.author = :author and b.pages >= 100 order by b.id")
    List<Books> findBooksByAuthorAndWherePagesIsGreaterThan(@Param("author") String author,
                                                            Pageable pageable);
}