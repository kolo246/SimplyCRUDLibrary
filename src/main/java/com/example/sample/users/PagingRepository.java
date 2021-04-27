package com.example.sample.users;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PagingRepository extends PagingAndSortingRepository<Users, Long> {
    @Query("select u from Users u where u.deleted=false order by u.id")
    List<Users> findAll(PageRequest page);
}
