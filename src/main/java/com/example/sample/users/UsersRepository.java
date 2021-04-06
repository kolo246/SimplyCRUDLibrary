package com.example.sample.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends CrudRepository<Users, Long> {
    List<Users> findAll();
    Optional<Users> findById(Long id);
    Optional<Users> findByName(String name);
}
