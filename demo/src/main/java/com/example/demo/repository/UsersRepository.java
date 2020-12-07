package com.example.demo.repository;

import com.example.demo.model.Users;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Id> {


}
