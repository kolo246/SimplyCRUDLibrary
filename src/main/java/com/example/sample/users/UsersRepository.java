package com.example.sample.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByIdAndDeletedIsFalse(Long id);
    Optional<Users> findByNameAndDeletedIsFalse(String name);
}
