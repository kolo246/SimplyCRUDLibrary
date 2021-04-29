package com.tango.down.users;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagingRepository extends PagingAndSortingRepository<Users, Long> {
    @Query("select u from Users u where u.deleted=false")
    List<Users> findAll(PageRequest page);
}
