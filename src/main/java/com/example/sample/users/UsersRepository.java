package com.example.sample.users;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByIdAndDeletedIsFalse(Long id);
    Optional<Users> findByNameAndDeletedIsFalse(String name);
}
