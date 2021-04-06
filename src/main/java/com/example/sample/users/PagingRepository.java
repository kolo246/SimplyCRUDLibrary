package com.example.sample.users;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PagingRepository extends PagingAndSortingRepository<Users,Long> {
}
